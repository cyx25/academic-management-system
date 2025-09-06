<template>
  <v-card class="mx-auto" outlined elevation="2">
    <v-card-title class="d-flex justify-space-between align-items-center">
      <span class="text-h6 font-weight-bold">{{ teste.title }}</span>
      <v-chip size="small" :color="teste.weight > 0 ? 'primary' : 'grey'" variant="tonal">
        {{ teste.weight }}%
      </v-chip>
    </v-card-title>
    
    <v-card-subtitle class="pb-2">
      <div class="d-flex align-center mb-1">
        <v-icon icon="mdi-calendar" size="small" class="mr-2"></v-icon>
        <span>{{ formatDate(teste.date) }}</span>
      </div>
      <div class="d-flex align-center">
        <v-icon icon="mdi-clock" size="small" class="mr-2"></v-icon>
        <span>{{ teste.duration }}</span>
      </div>
    </v-card-subtitle>

    <!-- Student Grade Display -->
    <v-card-text v-if="roleStore.isStudent && studentTestData" class="py-2">
      <v-alert type="info" variant="tonal" density="compact">
        <div class="d-flex align-center">
          <v-icon icon="mdi-star" class="mr-2"></v-icon>
          <span class="font-weight-medium">
            Grade: {{ studentTestData.grade !== null ? `${studentTestData.grade}/20` : 'Not graded' }}
          </span>
        </div>
      </v-alert>
      
      <!-- Revision Status Display -->
      <v-alert 
        v-if="studentTestData.hasRevision" 
        :type="getRevisionAlertType()" 
        variant="tonal" 
        density="compact" 
        class="mt-2"
      >
        <div class="d-flex align-center">
          <v-icon :icon="getRevisionIcon()" class="mr-2"></v-icon>
          <span>{{ getRevisionStatusText() }}</span>
        </div>
      </v-alert>
    </v-card-text>

    <!-- File Upload Section for Teachers -->
    <v-card-text v-if="roleStore.isMainTeacher" class="py-2">
      <div class="d-flex gap-2 mb-2">
        <v-btn
          v-if="!teste.statementFile"
          color="secondary"
          variant="outlined"
          size="small"
          prepend-icon="mdi-upload"
          @click="triggerStatementUpload"
          :loading="uploadingStatement"
        >
          Add Statement
        </v-btn>
        
        <v-btn
          v-if="!teste.correctionFile"
          color="primary"
          variant="outlined"
          size="small"
          prepend-icon="mdi-upload"
          @click="triggerCorrectionUpload"
          :loading="uploadingCorrection"
        >
          Add Correction
        </v-btn>
      </div>

      <input
        ref="statementFileInput"
        type="file"
        accept=".pdf,.doc,.docx"
        style="display: none"
        @change="uploadStatementFile"
      />
      <input
        ref="correctionFileInput"
        type="file"
        accept=".pdf,.doc,.docx"
        style="display: none"
        @change="uploadCorrectionFile"
      />
    </v-card-text>

    <v-card-actions class="pt-0">
      <!-- View Files -->
      <v-btn
        v-if="teste.statementFile"
        color="secondary"
        variant="outlined"
        size="small"
        prepend-icon="mdi-file-document-outline"
        @click="viewStatement"
      >
        Statement
      </v-btn>

      <v-btn
        v-if="teste.correctionFile"
        color="primary"
        variant="outlined"
        size="small"
        prepend-icon="mdi-file-check-outline"
        @click="viewCorrection"
      >
        Correction
      </v-btn>

      <v-spacer></v-spacer>

      <!-- Student Actions -->
      <v-btn
        v-if="roleStore.isStudent && canRequestRevision"
        color="warning"
        variant="outlined"
        size="small"
        prepend-icon="mdi-comment-question-outline"
        @click="openRevisionDialog"
      >
        Request Revision
      </v-btn>

      <v-btn
        v-if="roleStore.isStudent && studentTestData?.hasRevision && !studentTestData?.isPendingRevision"
        color="info"
        variant="outlined"
        size="small"
        prepend-icon="mdi-eye-outline"
        @click="viewRevisionStatus"
      >
        View Revision
      </v-btn>

      <!-- Teacher Action -->
      <v-btn
        v-if="roleStore.isTeacher"
        color="success"
        variant="elevated"
        size="small"
        prepend-icon="mdi-account-school-outline"
        @click="navigateToGrading"
      >
        Grade Students
      </v-btn>
    </v-card-actions>

    <RevisionDialog 
      v-if="studentTestData && revisionDialog"
      v-model="revisionDialog"
      :student-teste-id="studentTestData.id"
      @revision-requested="handleRevisionRequested"
    />

    <!-- Revision Status Dialog -->
    <v-dialog v-model="revisionStatusDialog" max-width="500px">
      <v-card v-if="revisionData">
        <v-card-title>Revision Status</v-card-title>
        <v-card-text>
          <div class="mb-3">
            <strong>Status:</strong> {{ revisionData.status }}
          </div>
          <div class="mb-3">
            <strong>Your Justification:</strong>
            <p>{{ revisionData.justification }}</p>
          </div>
          <div v-if="revisionData.newGrade !== null" class="mb-3">
            <strong>New Grade:</strong> {{ revisionData.newGrade }}/20
          </div>
          <div v-if="revisionData.reviewedBy">
            <strong>Reviewed by:</strong> {{ revisionData.reviewedBy }}
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="revisionStatusDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import RevisionDialog from './RevisionDialog.vue'
import type TesteDto from '@/models/TesteDto'
import type StudentTesteDto from '@/models/StudentTesteDto'
import type RevisionDto from '@/models/RevisionDto'

interface Props {
  teste: TesteDto
  studentTestData?: StudentTesteDto | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update': []
}>()

const router = useRouter()
const route = useRoute()
const roleStore = useRoleStore()
const revisionDialog = ref(false)
const revisionStatusDialog = ref(false)
const revisionData = ref<RevisionDto | null>(null)
const statementFileInput = ref<HTMLInputElement>()
const correctionFileInput = ref<HTMLInputElement>()
const uploadingStatement = ref(false)
const uploadingCorrection = ref(false)

const canRequestRevision = computed(() => {
  if (!roleStore.isStudent || !props.studentTestData) return false
  return props.studentTestData.canRequestRevision
})

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getRevisionAlertType(): 'info' | 'warning' | 'success' | 'error' {
  if (!props.studentTestData) return 'info'
  
  if (props.studentTestData.isPendingRevision) return 'warning'
  
  // You might need to fetch the actual revision to determine if it was approved/rejected
  return 'info'
}

function getRevisionIcon(): string {
  if (!props.studentTestData) return 'mdi-comment-question'
  
  if (props.studentTestData.isPendingRevision) return 'mdi-clock-outline'
  
  return 'mdi-comment-check'
}

function getRevisionStatusText(): string {
  if (!props.studentTestData) return 'Revision requested'
  
  if (props.studentTestData.isPendingRevision) return 'Revision pending review'
  
  return 'Revision processed'
}

function viewStatement() {
  if (props.teste.statementFile?.id) {
    const url = RemoteService.getFileUrl(props.teste.statementFile.id)
    window.open(url, '_blank')
  }
}

function viewCorrection() {
  if (props.teste.correctionFile?.id) {
    const url = RemoteService.getFileUrl(props.teste.correctionFile.id)
    window.open(url, '_blank')
  }
}

function triggerStatementUpload() {
  statementFileInput.value?.click()
}

function triggerCorrectionUpload() {
  correctionFileInput.value?.click()
}

async function uploadStatementFile(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file || !props.teste.id) return

  try {
    uploadingStatement.value = true
    await RemoteService.addStatementFile(props.teste.id, file)
    emit('update')
  } catch (error) {
    console.error('Failed to upload statement file:', error)
  } finally {
    uploadingStatement.value = false
  }
}

async function uploadCorrectionFile(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file || !props.teste.id) return

  try {
    uploadingCorrection.value = true
    await RemoteService.addCorrectionFile(props.teste.id, file)
    emit('update')
  } catch (error) {
    console.error('Failed to upload correction file:', error)
  } finally {
    uploadingCorrection.value = false
  }
}

function openRevisionDialog() {
  revisionDialog.value = true
}

async function viewRevisionStatus() {
  if (!props.studentTestData) return
  
  try {
    revisionData.value = await RemoteService.getTestRevisions(props.studentTestData.id)
    revisionStatusDialog.value = true
  } catch (error) {
    console.error('Failed to fetch revision data:', error)
  }
}

function navigateToGrading() {
  router.push({
    name: 'StudentTestsView',
    params: {
      id: route.params.id,
      testId: props.teste.id
    }
  })
}

function handleRevisionRequested() {
  emit('update')
}
</script>

<style scoped>
.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>