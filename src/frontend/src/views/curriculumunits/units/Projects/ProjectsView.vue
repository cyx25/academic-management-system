<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12" class="d-flex justify-space-between align-center">
        <h2 class="text-h5">Projects</h2>
        <v-btn
          v-if="roleStore.isMainTeacher"
          color="primary"
          prepend-icon="mdi-plus"
          @click="openCreateDialog"
        >
          Create Project
        </v-btn>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <v-row v-if="loading">
      <v-col>
        <div class="text-center">
          <v-progress-circular indeterminate></v-progress-circular>
          <p class="mt-4">Loading projects...</p>
        </div>
      </v-col>
    </v-row>

    <!-- Projects Grid -->
    <v-row v-else-if="projects.length > 0">
      <v-col v-for="project in projects" :key="project.id" cols="12" md="6" lg="4">
        <ProjectCard 
          :project="project" 
          @edit="openEditDialog" 
          @update="fetchProjects"
          @view-submissions="viewSubmissions"
        />
      </v-col>
    </v-row>
    
    <!-- Empty State -->
    <v-row v-else>
      <v-col>
        <v-alert type="info" variant="tonal">
          <v-icon icon="mdi-information"></v-icon>
          No projects found for this curriculum unit.
        </v-alert>
      </v-col>
    </v-row>

    <!-- Project Dialog -->
    <ProjectDialog
      v-model="dialog"
      :project="editedProject"
      :is-edit="isEdit"
      @save="onProjectSave"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type ProjectDto from '@/models/ProjectDto'
import ProjectCard from './ProjectCard.vue'
import ProjectDialog from './ProjectDialog.vue'

const route = useRoute()
const router = useRouter()
const roleStore = useRoleStore()
const unitId = Number(route.params.id)

const projects = ref<ProjectDto[]>([])
const dialog = ref(false)
const isEdit = ref(false)
const editedProject = ref<ProjectDto | null>(null)
const loading = ref(true)

onMounted(fetchProjects)

async function fetchProjects() {
  try {
    loading.value = true
    console.log('Fetching projects for unit:', unitId)
    projects.value = await RemoteService.getProjects(unitId)
    console.log('Fetched projects:', projects.value.length)
  } catch (error) {
    console.error('Failed to fetch projects:', error)
    // You might want to show a snackbar or alert here
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  isEdit.value = false
  editedProject.value = {
    id: null,
    title: '',
    weight: 25,
    dueDate: new Date().toISOString().split('T')[0] + 'T23:59',
    maxGroupSize: 1,
    statementFile: null,
  }
  dialog.value = true
}

function openEditDialog(project: ProjectDto) {
  isEdit.value = true
  editedProject.value = { ...project }
  dialog.value = true
}

async function onProjectSave() {
  dialog.value = false
  await fetchProjects()
}

function viewSubmissions(project: ProjectDto) {
  if (project.id) {
    router.push({
      name: 'project-submissions',
      params: {
        id: unitId,
        projectId: project.id
      }
    })
  }
}
</script>