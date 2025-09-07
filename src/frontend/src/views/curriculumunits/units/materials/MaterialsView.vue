<template>
  <v-container fluid class="pa-6">
    <v-row class="mb-4">
      <v-col cols="12" class="d-flex justify-space-between align-items-center">
        <div>
          <h1 class="text-h4 font-weight-bold text-primary mb-2">Materials</h1>
          <p class="text-body-1 text-medium-emphasis">
            {{ roleStore.isStudent ? 'Access course materials and resources' : 'Manage and organize curriculum unit materials' }}
          </p>
        </div>
        <v-btn
          v-if="roleStore.isTeacher"
          color="primary"
          prepend-icon="mdi-plus"
          size="large"
          @click="openCreateDialog"
        >
          Add Material
        </v-btn>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <v-row v-if="loading">
      <v-col>
        <div class="text-center py-8">
          <v-progress-circular indeterminate size="48" color="primary" class="mb-3"></v-progress-circular>
          <p class="text-body-1 text-medium-emphasis">Loading materials...</p>
        </div>
      </v-col>
    </v-row>

    <!-- Materials Tabs -->
    <v-row v-else-if="materials.length > 0">
      <v-col cols="12">
        <v-card elevation="2">
          <v-tabs v-model="activeTab" color="primary" density="compact">
            <v-tab
              v-for="(material, index) in materials"
              :key="material.id"
              :value="index"
              class="material-tab"
            >
              <v-icon :icon="material.iconName" size="18" class="mr-2"></v-icon>
              {{ material.name }}
              <v-btn
                v-if="roleStore.isTeacher"
                icon="mdi-close"
                size="x-small"
                variant="text"
                class="ml-2 delete-tab-btn"
                @click.stop="deleteMaterial(material.id!)"
              ></v-btn>
            </v-tab>
          </v-tabs>

          <v-tabs-window v-model="activeTab">
            <v-tabs-window-item
              v-for="(material, index) in materials"
              :key="material.id"
              :value="index"
            >
              <MaterialTabContent 
                :material="material" 
                :can-edit="roleStore.isTeacher"
                @file-uploaded="refreshMaterials"
                @file-deleted="refreshMaterials"
              />
            </v-tabs-window-item>
          </v-tabs-window>
        </v-card>
      </v-col>
    </v-row>

    <!-- Empty State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="info" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-book-open-outline" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">No Materials Available</h3>
          <p class="text-body-1 mb-4">
            {{ roleStore.isTeacher 
              ? 'Add your first material to help students learn.' 
              : 'Materials will appear here when teachers add them.' 
            }}
          </p>
          <v-btn
            v-if="roleStore.isTeacher"
            color="primary"
            prepend-icon="mdi-plus"
            @click="openCreateDialog"
          >
            Add First Material
          </v-btn>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Create Material Dialog -->
    <CreateMaterialDialog
      v-model="createDialog"
      :unit-id="unitId"
      @material-created="onMaterialCreated"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type MaterialDto from '@/models/MaterialDto'
import MaterialTabContent from './MaterialTabContent.vue'
import CreateMaterialDialog from './CreateMaterialDialog.vue'

const route = useRoute()
const roleStore = useRoleStore()
const unitId = Number(route.params.id)

const materials = ref<MaterialDto[]>([])
const activeTab = ref(0)
const loading = ref(true)
const createDialog = ref(false)

onMounted(fetchMaterials)

async function fetchMaterials() {
  try {
    loading.value = true
    materials.value = await RemoteService.getMaterials(unitId)
  } catch (error) {
    console.error('Failed to fetch materials:', error)
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  createDialog.value = true
}

async function onMaterialCreated() {
  createDialog.value = false
  await refreshMaterials()
  // Set active tab to the newly created material
  activeTab.value = materials.value.length - 1
}

async function refreshMaterials() {
  await fetchMaterials()
}

async function deleteMaterial(materialId: number) {
  if (confirm('Are you sure you want to delete this material tab? All files will be permanently removed.')) {
    try {
      await RemoteService.deleteMaterial(materialId)
      await refreshMaterials()
      // Reset tab if needed
      if (activeTab.value >= materials.value.length) {
        activeTab.value = Math.max(0, materials.value.length - 1)
      }
    } catch (error) {
      console.error('Failed to delete material:', error)
      alert('Failed to delete material tab.')
    }
  }
}
</script>

<style scoped>
.material-tab {
  position: relative;
}

.delete-tab-btn {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.material-tab:hover .delete-tab-btn {
  opacity: 1;
}

.delete-tab-btn:hover {
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
}
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}
</style>