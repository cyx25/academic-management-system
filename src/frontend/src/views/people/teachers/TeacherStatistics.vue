<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold mb-2">Estatísticas do Professor</h1>
          <p class="text-h6 text-medium-emphasis">Resumo da sua atividade académica</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar estatísticas...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- Statistics Dashboard -->
        <div v-else-if="statistics">
          <!-- Main Statistics Cards -->
          <v-row class="mb-6">
            <!-- Curriculum Units -->
            <v-col cols="12" md="6" lg="3">
              <v-card 
                class="text-center pa-4" 
                elevation="8" 
                rounded="xl" 
                color="red"
                variant="elevated"
              >
                <v-card-text>
                  <v-icon 
                    icon="mdi-school" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.totalCurriculumUnits }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Unidades Curriculares</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Projects Created -->
            <v-col cols="12" md="6" lg="3">
              <v-card 
                class="text-center pa-4" 
                elevation="8" 
                rounded="xl" 
                color="orange"
                variant="elevated"
              >
                <v-card-text>
                  <v-icon 
                    icon="mdi-folder-multiple" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.totalProjectsCreated }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Projetos Criados</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Tests Created -->
            <v-col cols="12" md="6" lg="3">
              <v-card 
                class="text-center pa-4" 
                elevation="8" 
                rounded="xl" 
                color="blue"
                variant="elevated"
              >
                <v-card-text>
                  <v-icon 
                    icon="mdi-file-document" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.totalTestsCreated }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Testes Criados</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Total Assessments -->
            <v-col cols="12" md="6" lg="3">
              <v-card 
                class="text-center pa-4" 
                elevation="8" 
                rounded="xl" 
                color="success"
                variant="elevated"
              >
                <v-card-text>
                  <v-icon 
                    icon="mdi-clipboard-list" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ getTotalAssessments() }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Total Avaliações</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <!-- Detailed Cards -->
          <v-row>
            <!-- Assessment Breakdown -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-chart-pie" color="primary" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Distribuição de Avaliações</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <!-- Projects -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-folder-outline" size="16" class="mr-1" color="orange"></v-icon>
                        Projetos
                      </span>
                      <span class="text-h6 font-weight-bold">
                        {{ statistics.totalProjectsCreated }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getProjectsPercentage()"
                      color="orange"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                  </div>

                  <!-- Tests -->
                  <div>
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-file-document-outline" size="16" class="mr-1" color="blue"></v-icon>
                        Testes
                      </span>
                      <span class="text-h6 font-weight-bold">
                        {{ statistics.totalTestsCreated }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getTestsPercentage()"
                      color="blue"
                      height="8"
                      rounded
                    ></v-progress-linear>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Revisions Statistics -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-file-edit" color="primary" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Revisões Processadas</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <!-- Accepted Revisions -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-check-circle" size="16" class="mr-1" color="success"></v-icon>
                        Aceites
                      </span>
                      <span class="text-h6 font-weight-bold text-success">
                        {{ statistics.revisionsAccepted }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getAcceptedRevisionsPercentage()"
                      color="success"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                  </div>

                  <!-- Denied Revisions -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-close-circle" size="16" class="mr-1" color="error"></v-icon>
                        Rejeitadas
                      </span>
                      <span class="text-h6 font-weight-bold text-error">
                        {{ statistics.revisionsDenied }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getDeniedRevisionsPercentage()"
                      color="error"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                  </div>

                  <!-- Acceptance Rate -->
                  <v-divider class="my-3"></v-divider>
                  <div class="text-center">
                    <p class="text-body-2 text-medium-emphasis mb-1">Taxa de Aceitação</p>
                    <p class="text-h4 font-weight-bold" :class="getAcceptanceRateColor()">
                      {{ getRevisionAcceptanceRate().toFixed(1) }}%
                    </p>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

         
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoleStore } from '@/stores/role'
import RemoteServices from '@/services/RemoteService'
import type { TeacherStatisticsDto } from '@/models/TeacherViewsDto'

const roleStore = useRoleStore()

const statistics = ref<TeacherStatisticsDto | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  await fetchStatistics()
})

async function fetchStatistics() {
  try {
    loading.value = true
    error.value = null
    
    const teacherId = roleStore.getCurrentUserID
    if (!teacherId) {
      throw new Error('ID do professor não disponível')
    }
    
    statistics.value = await RemoteServices.getTeacherStatistics(teacherId)
    console.log('Statistics loaded:', statistics.value)
  } catch (err: any) {
    console.error('Failed to fetch statistics:', err)
    error.value = err.message || 'Erro ao carregar estatísticas'
  } finally {
    loading.value = false
  }
}

function getTotalAssessments(): number {
  if (!statistics.value) return 0
  return statistics.value.totalProjectsCreated + statistics.value.totalTestsCreated
}

function getProjectsPercentage(): number {
  const total = getTotalAssessments()
  if (total === 0) return 0
  return (statistics.value!.totalProjectsCreated / total) * 100
}

function getTestsPercentage(): number {
  const total = getTotalAssessments()
  if (total === 0) return 0
  return (statistics.value!.totalTestsCreated / total) * 100
}

function getTotalRevisions(): number {
  if (!statistics.value) return 0
  return statistics.value.revisionsAccepted + statistics.value.revisionsDenied
}

function getAcceptedRevisionsPercentage(): number {
  const total = getTotalRevisions()
  if (total === 0) return 0
  return (statistics.value!.revisionsAccepted / total) * 100
}

function getDeniedRevisionsPercentage(): number {
  const total = getTotalRevisions()
  if (total === 0) return 0
  return (statistics.value!.revisionsDenied / total) * 100
}

function getRevisionAcceptanceRate(): number {
  const total = getTotalRevisions()
  if (total === 0) return 0
  return (statistics.value!.revisionsAccepted / total) * 100
}

function getAcceptanceRateColor(): string {
  const rate = getRevisionAcceptanceRate()
  if (rate >= 80) return 'text-success'
  if (rate >= 60) return 'text-warning'
  return 'text-error'
}

function getSummaryMessage(): string {
  if (!statistics.value) return 'Carregando...'
  
  const totalAssessments = getTotalAssessments()
  const totalUnits = statistics.value.totalCurriculumUnits
  
  if (totalUnits >= 5) return 'Professor Experiente'
  if (totalAssessments >= 20) return 'Criador Ativo'
  if (totalAssessments >= 10) return 'Educador Dedicado'
  return 'Em Crescimento'
}
</script>