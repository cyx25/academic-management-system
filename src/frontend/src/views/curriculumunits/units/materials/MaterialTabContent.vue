<template>
  <v-card-text class="pa-6">
    <div class="d-flex justify-space-between align-center mb-4">
      <div>
        <h2 class="text-h6 font-weight-medium mb-1">{{ material.name }}</h2>
        <p class="text-body-2 text-medium-emphasis mb-0">
          {{ material.files.length }} file{{ material.files.length !== 1 ? 's' : '' }}
        </p>
      </div>
      <v-btn
        v-if="canEdit"
        color="primary"
        variant="tonal"
        prepend-icon="mdi-upload"
        @click="triggerFileUpload"
      >
        Add File
      </v-btn>
    </div>

    <v-divider class="mb-4"></v-divider>

    <!-- File Upload Input (Hidden) -->
    <input
      ref="fileInput"
      type="file"
      multiple
      @change="handleFileUpload"
      style="display: none"
    />

    <!-- Files List -->
    <div v-if="material.files.length > 0">
      <v-list lines="two" density="compact">
        <v-list-item
          v-for="file in material.files"
          :key="file.id"
          class="file-item mb-2 pa-3"
          rounded="lg"
        >
          <template v-slot:prepend>
            <v-avatar 
              :color="getFileTypeColor(file.contentType)" 
              size="40"
              variant="tonal"
            >
              <v-icon :icon="getFileIcon(file.contentType)" size="20"></v-icon>
            </v-avatar>
          </template>

          <v-list-item-title class="text-body-1 font-weight-medium">
            {{ file.fileName }}
          </v-list-item-title>
          
          <v-list-item-subtitle class="text-body-2 text-medium-emphasis">
            {{ formatFileSize(file.size) }} • {{ formatContentType(file.contentType) }}
          </v-list-item-subtitle>

          <template v-slot:append>
            <div class="d-flex align-center gap-2">
              <v-btn
                icon="mdi-download"
                variant="tonal"
                color="primary"
                size="small"
                @click="downloadFile(file.id)"
              ></v-btn>
              <v-btn
                v-if="canEdit"
                icon="mdi-delete"
                variant="tonal"
                color="error"
                size="small"
                @click="deleteFile(file.id)"
              ></v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-8">
      <v-icon icon="mdi-file-outline" size="48" color="surface-variant" class="mb-3"></v-icon>
      <p class="text-body-1 font-weight-medium mb-1">No files yet</p>
      <p class="text-body-2 text-medium-emphasis mb-0">
        {{ canEdit ? 'Click "Add File" to upload your first file.' : 'Files will appear here when teachers add them.' }}
      </p>
    </div>

    <!-- Upload Progress -->
    <v-overlay v-model="uploading" class="align-center justify-center">
      <div class="text-center">
        <v-progress-circular indeterminate size="64" color="primary" class="mb-3"></v-progress-circular>
        <p class="text-h6 text-white">Uploading files...</p>
      </div>
    </v-overlay>
  </v-card-text>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import RemoteService from '@/services/RemoteService'
import type MaterialDto from '@/models/MaterialDto'
import type FileDto from '@/models/FileDto'

interface Props {
  material: MaterialDto
  canEdit: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  fileUploaded: []
  fileDeleted: []
}>()

const fileInput = ref<HTMLInputElement>()
const uploading = ref(false)

function triggerFileUpload() {
  fileInput.value?.click()
}

async function handleFileUpload(event: Event) {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  try {
    uploading.value = true
    
    // Upload files one by one
    for (const file of Array.from(input.files)) {
      await RemoteService.addFileToMaterial(props.material.id!, file)
    }
    
    emit('fileUploaded')
    
    // Reset input
    input.value = ''
  } catch (error) {
    console.error('Failed to upload files:', error)
    alert('Failed to upload files. Please try again.')
  } finally {
    uploading.value = false
  }
}

async function deleteFile(fileId: number) {
  if (confirm('Are you sure you want to delete this file?')) {
    try {
      await RemoteService.deleteFile(fileId)
      emit('fileDeleted')
    } catch (error) {
      console.error('Failed to delete file:', error)
      alert('Failed to delete file.')
    }
  }
}

function downloadFile(fileId: number) {
  const url = RemoteService.getFileDownloadUrl(fileId)
  window.open(url, '_blank')
}

function formatFileSize(sizeKb: number): string {
  if (sizeKb < 1024) return `${sizeKb} KB`
  else return `${(sizeKb / 1024).toFixed(1)} MB`
}

function formatContentType(contentType: string): string {
  const types: { [key: string]: string } = {
    'application/pdf': 'PDF',
    'application/msword': 'Word',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word',
    'application/vnd.ms-excel': 'Excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel',
    'application/vnd.ms-powerpoint': 'PowerPoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'PowerPoint',
    'image/jpeg': 'Image',
    'image/png': 'Image',
    'image/gif': 'Image',
    'text/plain': 'Text',
    'application/zip': 'Archive'
  }
  
  return types[contentType] || 'File'
}

function getFileIcon(contentType: string): string {
  if (contentType.includes('pdf')) return 'mdi-file-pdf-box'
  if (contentType.includes('word')) return 'mdi-file-word-box'
  if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'mdi-file-excel-box'
  if (contentType.includes('powerpoint') || contentType.includes('presentation')) return 'mdi-file-powerpoint-box'
  if (contentType.includes('image')) return 'mdi-file-image-box'
  if (contentType.includes('zip') || contentType.includes('archive')) return 'mdi-file-zip-box'
  if (contentType.includes('text')) return 'mdi-file-document-box'
  return 'mdi-file-box'
}

function getFileTypeColor(contentType: string): string {
  if (contentType.includes('pdf')) return 'red'
  if (contentType.includes('word')) return 'blue'
  if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'green'
  if (contentType.includes('powerpoint') || contentType.includes('presentation')) return 'orange'
  if (contentType.includes('image')) return 'purple'
  if (contentType.includes('zip') || contentType.includes('archive')) return 'amber'
  return 'grey'
}
</script>

<style scoped>
.file-item {
  background: rgb(var(--v-theme-surface-variant));
  border: 1px solid rgb(var(--v-border-color));
  transition: all 0.2s ease;
}

.file-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
</style>