<template>
  <v-container fluid class="pa-6">
    <!-- Header -->
    <v-row class="mb-4">
      <v-col cols="12" class="d-flex justify-space-between align-items-center">
        <div>
          <h1 class="text-h4 font-weight-bold text-primary mb-2">Projetos</h1>
          <p class="text-body-1 text-medium-emphasis">
            {{ roleStore.isStudent ? 'Ver e entregar os teus projetos' : 'Gerir projetos da unidade curricular e submissões dos alunos' }}
          </p>
        </div>
        <v-btn
          v-if="roleStore.isMainTeacher"
          color="primary"
          prepend-icon="mdi-plus"
          size="large"
          @click="openCreateDialog"
        >
          Criar Projeto
        </v-btn>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <p class="text-h6 mt-4">Loading projects...</p>
    </div>

    <!-- Projects Grid -->
    <v-row v-else-if="projects.length > 0">
      <v-col v-for="project in projects" :key="project.id" cols="12" sm="6" lg="4">
        <ProjectCard :project="project" @update="fetchProjects" />
      </v-col>
    </v-row>

    <!-- Empty State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="info" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-folder-outline" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">Sem Projetos Disponíveis</h3>
          <p class="text-body-1 mb-4">
            {{ roleStore.isMainTeacher 
              ? 'Crie o seu primeiro projeto para começar a trabalhar nas atribuições.' 
              : 'Os projetos aparecerão aqui quando os professores os criarem.' 
            }}
          </p>
          <v-btn
            v-if="roleStore.isMainTeacher"
            color="primary"
            prepend-icon="mdi-plus"
            @click="openCreateDialog"
          >
            Criar Primeiro Projeto
          </v-btn>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Create Project Dialog - Using exact same structure as before -->
    <ProjectDialog 
      v-model="createDialog" 
      :project="null"
      :is-edit="false"
      @save="handleProjectCreated" 
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import ProjectCard from './ProjectCard.vue'
import ProjectDialog from './ProjectDialog.vue'
import type ProjectDto from '@/models/ProjectDto'

const route = useRoute()
const roleStore = useRoleStore()

const unitId = computed(() => Number(route.params.id))
const projects = ref<ProjectDto[]>([])
const loading = ref(true)
const createDialog = ref(false)

onMounted(async () => {
  await fetchProjects()
})

async function fetchProjects() {
  try {
    loading.value = true
    projects.value = await RemoteService.getProjects(unitId.value)
  } catch (error) {
    console.error('Failed to fetch projects:', error)
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  createDialog.value = true
}

async function handleProjectCreated() {
  createDialog.value = false
  await fetchProjects()
}
</script>

<style scoped>
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}
</style>