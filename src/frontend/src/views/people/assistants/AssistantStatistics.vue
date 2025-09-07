<!-- filepath: /home/paulo/DEI/candidatura-projeto/projeto-dei/src/frontend/src/views/people/assistants/AssistantStatistics.vue -->
<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold mb-2">Estatísticas do Assistente</h1>
          <p class="text-h6 text-medium-emphasis">Resumo da sua atividade de correção</p>
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

            <!-- Tests Graded -->
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
                    icon="mdi-file-document-check" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.testsGraded }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Testes Corrigidos</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Projects Graded -->
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
                    icon="mdi-folder-check" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.projectsGraded }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Projetos Corrigidos</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Revisions Graded -->
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
                    icon="mdi-file-edit" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ statistics.revisionsGraded }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Revisões Processadas</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <!-- Detailed Analytics -->
          <v-row>
            <!-- Grading Distribution -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-chart-pie" color="primary" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Distribuição de Correções</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <!-- Tests -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-file-document" size="16" class="mr-1" color="blue"></v-icon>
                        Testes
                      </span>
                      <span class="text-h6 font-weight-bold">
                        {{ statistics.testsGraded }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getTestsPercentage()"
                      color="blue"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                  </div>

                  <!-- Projects -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-folder" size="16" class="mr-1" color="orange"></v-icon>
                        Projetos
                      </span>
                      <span class="text-h6 font-weight-bold">
                        {{ statistics.projectsGraded }}
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

                  <!-- Revisions -->
                  <div>
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-file-edit" size="16" class="mr-1" color="success"></v-icon>
                        Revisões
                      </span>
                      <span class="text-h6 font-weight-bold">
                        {{ statistics.revisionsGraded }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getRevisionsPercentage()"
                      color="success"
                      height="8"
                      rounded
                    ></v-progress-linear>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Performance Metrics -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-chart-line" color="primary" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Métricas de Performance</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <!-- Total Grading Activities -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">Total de Atividades</span>
                      <span class="text-h5 font-weight-bold text-primary">
                        {{ getTotalGradingActivities() }}
                      </span>
                    </div>
                    <p class="text-caption text-medium-emphasis mb-0">
                      Soma de todas as correções realizadas
                    </p>
                  </div>

                  <v-divider class="my-3"></v-divider>

                  <!-- Average per Unit -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">Média por Unidade</span>
                      <span class="text-h5 font-weight-bold text-info">
                        {{ getAverageGradingPerUnit().toFixed(1) }}
                      </span>
                    </div>
                    <p class="text-caption text-medium-emphasis mb-0">
                      Correções por unidade curricular
                    </p>
                  </div>

                  <v-divider class="my-3"></v-divider>

                  <!-- Assessment vs Revisions Ratio -->
                  <div>
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">Rácio Avaliações/Revisões</span>
                      <span class="text-h5 font-weight-bold text-success">
                        {{ getAssessmentRevisionRatio() }}
                      </span>
                    </div>
                    <p class="text-caption text-medium-emphasis mb-0">
                      Proporção entre avaliações e revisões
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
import type { AssistantStatisticsDto } from '@/models/AssistantViewsDto'

const roleStore = useRoleStore()

const statistics = ref<AssistantStatisticsDto | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  await fetchStatistics()
})

async function fetchStatistics() {
  try {
    loading.value = true
    error.value = null
    
    const assistantId = roleStore.getCurrentUserID
    if (!assistantId) {
      throw new Error('ID do assistente não disponível')
    }
    
    statistics.value = await RemoteServices.getAssistantStatistics(assistantId)
    console.log('Statistics loaded:', statistics.value)
  } catch (err: any) {
    console.error('Failed to fetch statistics:', err)
    error.value = err.message || 'Erro ao carregar estatísticas'
  } finally {
    loading.value = false
  }
}

function getTotalGradingActivities(): number {
  if (!statistics.value) return 0
  return statistics.value.testsGraded + statistics.value.projectsGraded + statistics.value.revisionsGraded
}

function getTotalAssessmentsGraded(): number {
  if (!statistics.value) return 0
  return statistics.value.testsGraded + statistics.value.projectsGraded
}

function getTestsPercentage(): number {
  const total = getTotalGradingActivities()
  if (total === 0) return 0
  return (statistics.value!.testsGraded / total) * 100
}

function getProjectsPercentage(): number {
  const total = getTotalGradingActivities()
  if (total === 0) return 0
  return (statistics.value!.projectsGraded / total) * 100
}

function getRevisionsPercentage(): number {
  const total = getTotalGradingActivities()
  if (total === 0) return 0
  return (statistics.value!.revisionsGraded / total) * 100
}

function getAverageGradingPerUnit(): number {
  if (!statistics.value || statistics.value.totalCurriculumUnits === 0) return 0
  return getTotalGradingActivities() / statistics.value.totalCurriculumUnits
}

function getAssessmentRevisionRatio(): string {
  if (!statistics.value || statistics.value.revisionsGraded === 0) return '∞'
  const assessments = getTotalAssessmentsGraded()
  const ratio = assessments / statistics.value.revisionsGraded
  return ratio.toFixed(1) + ':1'
}




</script>