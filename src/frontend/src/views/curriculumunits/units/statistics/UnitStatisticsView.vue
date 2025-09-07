<!-- filepath: /home/paulo/DEI/candidatura-projeto/projeto-dei/src/frontend/src/views/curriculumunits/units/statistics/UnitStatisticsView.vue -->
<template>
  <v-container fluid class="pa-6">
    <!-- Header -->
    <div class="text-center mb-8">
      <h1 class="text-h3 font-weight-bold mb-2">Estatísticas da Unidade Curricular</h1>
      <p class="text-h6 text-medium-emphasis">Análise detalhada de desempenho e distribuição</p>
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

    <!-- No Data State -->
    <v-card v-else-if="!statistics || statistics.finalGrades.length === 0" class="text-center py-8" elevation="4" rounded="lg">
      <v-card-text>
        <v-icon icon="mdi-chart-line-variant" size="64" color="info" class="mb-4"></v-icon>
        <h3 class="text-h5 mb-2">Sem dados estatísticos</h3>
        <p class="text-body-1 text-medium-emphasis">Ainda não há avaliações suficientes para gerar estatísticas.</p>
      </v-card-text>
    </v-card>

    <!-- Statistics Dashboard -->
    <div v-else>
      <!-- Summary Cards -->
      <v-row class="mb-6">
        <v-col cols="12" md="3">
          <v-card elevation="6" rounded="lg" color="red" variant="elevated" class="h-100">
            <v-card-text class="pa-4 text-center">
              <v-icon icon="mdi-chart-line" size="32" color="white" class="mb-2"></v-icon>
              <h3 class="text-h4 font-weight-bold text-white mb-1">{{ overallAverage }}</h3>
              <p class="text-body-2 text-white opacity-90 mb-0">Média Geral</p>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="3">
          <v-card elevation="6" rounded="lg" color="info" variant="elevated" class="h-100">
            <v-card-text class="pa-4 text-center">
              <v-icon icon="mdi-account-group" size="32" color="white" class="mb-2"></v-icon>
              <h3 class="text-h4 font-weight-bold text-white mb-1">{{ statistics.finalGrades.length }}</h3>
              <p class="text-body-2 text-white opacity-90 mb-0">Total de Alunos</p>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="3">
          <v-card elevation="6" rounded="lg" color="success" variant="elevated" class="h-100">
            <v-card-text class="pa-4 text-center">
              <v-icon icon="mdi-check-circle" size="32" color="white" class="mb-2"></v-icon>
              <h3 class="text-h4 font-weight-bold text-white mb-1">{{ passingRate }}%</h3>
              <p class="text-body-2 text-white opacity-90 mb-0">Taxa de Aprovação</p>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="3">
          <v-card elevation="6" rounded="lg" color="secondary" variant="elevated" class="h-100">
            <v-card-text class="pa-4 text-center">
              <v-icon icon="mdi-clipboard-list" size="32" color="white" class="mb-2"></v-icon>
              <h3 class="text-h4 font-weight-bold text-white mb-1">{{ statistics.assessmentAverages.length }}</h3>
              <p class="text-body-2 text-white opacity-90 mb-0">Total de Avaliações</p>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Main Analytics Row -->
      <v-row class="mb-6">
        <!-- Grade Distribution -->
        <v-col cols="12" lg="6">
          <v-card elevation="6" rounded="lg" class="h-100">
            <v-card-title class="d-flex align-center pa-4">
              <v-icon icon="mdi-chart-donut" color="primary" class="mr-3"></v-icon>
              <h3 class="text-h5 font-weight-bold">Distribuição de Notas</h3>
            </v-card-title>
            <v-card-text class="pa-4">
              <div class="mb-4">
                <!-- Excellent -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-star" size="16" class="mr-1" color="purple"></v-icon>
                      Excelente (18-20)
                    </span>
                    <span class="text-h6 font-weight-bold">{{ gradeDistribution.excellent }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getGradePercentage(gradeDistribution.excellent)"
                    color="purple"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Very Good -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-thumb-up" size="16" class="mr-1" color="green"></v-icon>
                      Muito Bom (16-17)
                    </span>
                    <span class="text-h6 font-weight-bold">{{ gradeDistribution.veryGood }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getGradePercentage(gradeDistribution.veryGood)"
                    color="green"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Good -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-check" size="16" class="mr-1" color="blue"></v-icon>
                      Bom (14-15)
                    </span>
                    <span class="text-h6 font-weight-bold">{{ gradeDistribution.good }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getGradePercentage(gradeDistribution.good)"
                    color="blue"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Satisfactory -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-minus" size="16" class="mr-1" color="orange"></v-icon>
                      Satisfatório (10-13)
                    </span>
                    <span class="text-h6 font-weight-bold">{{ gradeDistribution.satisfactory }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getGradePercentage(gradeDistribution.satisfactory)"
                    color="orange"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Insufficient -->
                <div>
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-close" size="16" class="mr-1" color="red"></v-icon>
                      Insuficiente (0-9)
                    </span>
                    <span class="text-h6 font-weight-bold">{{ gradeDistribution.insufficient }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getGradePercentage(gradeDistribution.insufficient)"
                    color="red"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Revision Statistics -->
        <v-col cols="12" lg="6">
          <v-card elevation="6" rounded="lg" class="h-100">
            <v-card-title class="d-flex align-center pa-4">
              <v-icon icon="mdi-comment-question" color="primary" class="mr-3"></v-icon>
              <h3 class="text-h5 font-weight-bold">Estatísticas de Revisões</h3>
            </v-card-title>
            <v-card-text class="pa-4">
              <div class="mb-4">
                <!-- Total Submitted -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-file-document" size="16" class="mr-1" color="info"></v-icon>
                      Total Submetidas
                    </span>
                    <span class="text-h6 font-weight-bold">{{ statistics.revisionStatistics.totalSubmitted }}</span>
                  </div>
                </div>

                <!-- Approved -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-check-circle" size="16" class="mr-1" color="success"></v-icon>
                      Aprovadas
                    </span>
                    <span class="text-h6 font-weight-bold">{{ statistics.revisionStatistics.totalApproved }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getRevisionPercentage(statistics.revisionStatistics.totalApproved)"
                    color="success"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Denied -->
                <div class="mb-3">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-close-circle" size="16" class="mr-1" color="error"></v-icon>
                      Rejeitadas
                    </span>
                    <span class="text-h6 font-weight-bold">{{ statistics.revisionStatistics.totalDenied }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getRevisionPercentage(statistics.revisionStatistics.totalDenied)"
                    color="error"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>

                <!-- Pending -->
                <div>
                  <div class="d-flex justify-space-between align-center mb-1">
                    <span class="text-body-1 font-weight-medium">
                      <v-icon icon="mdi-clock" size="16" class="mr-1" color="warning"></v-icon>
                      Pendentes
                    </span>
                    <span class="text-h6 font-weight-bold">{{ statistics.revisionStatistics.totalPending }}</span>
                  </div>
                  <v-progress-linear
                    :model-value="getRevisionPercentage(statistics.revisionStatistics.totalPending)"
                    color="warning"
                    height="8"
                    rounded
                  ></v-progress-linear>
                </div>
              </div>

              <!-- Revision Rates -->
              <v-divider class="my-3"></v-divider>
              <div class="text-center">
                <div class="mb-2">
                  <span class="text-h6 font-weight-bold text-success">
                    {{ getApprovalRate() }}%
                  </span>
                  <span class="text-body-2 text-medium-emphasis ml-1">Taxa de Aprovação</span>
                </div>
                <div>
                  <span class="text-h6 font-weight-bold text-error">
                    {{ getDenialRate() }}%
                  </span>
                  <span class="text-body-2 text-medium-emphasis ml-1">Taxa de Rejeição</span>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Assessment Averages -->
      <v-row>
        <v-col cols="12">
          <v-card elevation="6" rounded="lg">
            <v-card-title class="d-flex align-center pa-4">
              <v-icon icon="mdi-chart-bar" color="primary" class="mr-3"></v-icon>
              <h3 class="text-h5 font-weight-bold">Médias por Avaliação</h3>
            </v-card-title>
            <v-card-text class="pa-4">
              <v-row v-if="statistics.assessmentAverages.length > 0">
                <v-col 
                  v-for="assessment in statistics.assessmentAverages" 
                  :key="`${assessment.assessmentType}-${assessment.assessmentTitle}`"
                  cols="12" 
                  md="6" 
                  lg="4"
                >
                  <v-card 
                    elevation="2" 
                    rounded="lg" 
                    :color="getAssessmentColor(assessment.assessmentType)" 
                    variant="tonal"
                    class="h-100"
                  >
                    <v-card-text class="pa-4">
                      <div class="d-flex align-center mb-3">
                        <v-icon 
                          :icon="getAssessmentIcon(assessment.assessmentType)" 
                          :color="getAssessmentColor(assessment.assessmentType)"
                          size="24" 
                          class="mr-3"
                        ></v-icon>
                        <div>
                          <h4 class="text-h6 font-weight-bold line-clamp-1">
                            {{ assessment.assessmentTitle }}
                          </h4>
                          <p class="text-caption text-medium-emphasis mb-0">
                            {{ getAssessmentLabel(assessment.assessmentType) }}
                          </p>
                        </div>
                      </div>

                      <div class="text-center mb-3">
                        <h3 class="text-h3 font-weight-bold" :class="`text-${getAssessmentColor(assessment.assessmentType)}`">
                          {{ assessment.averageGrade ? assessment.averageGrade.toFixed(1) : 'N/A' }}
                        </h3>
                        <p class="text-body-2 text-medium-emphasis">Média</p>
                      </div>

                      <div class="mb-2">
                        <div class="d-flex justify-space-between text-caption mb-1">
                          <span>Corrigidos</span>
                          <span>{{ assessment.totalGraded }}/{{ assessment.totalStudents }}</span>
                        </div>
                        <v-progress-linear
                          :model-value="getCompletionPercentage(assessment)"
                          :color="getAssessmentColor(assessment.assessmentType)"
                          height="6"
                          rounded
                        ></v-progress-linear>
                      </div>

                      <div class="text-center">
                        <v-chip 
                          :color="getCompletionChipColor(assessment)"
                          variant="elevated"
                          size="small"
                        >
                          {{ getCompletionPercentage(assessment).toFixed(1) }}% Completo
                        </v-chip>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
              
              <v-alert v-else type="info" variant="tonal" class="text-center">
                <v-icon icon="mdi-information" class="mb-2"></v-icon>
                <h4 class="text-h6 mb-1">Nenhuma avaliação encontrada</h4>
                <p class="text-body-2 mb-0">Ainda não há avaliações criadas para esta unidade curricular.</p>
              </v-alert>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import RemoteService from '@/services/RemoteService'
import type { CurriculumUnitStatisticsDto, GradeDistributionDto } from '@/models/CurriculumUnitStatistics'
import type { AssessmentAverageDto } from '@/models/CurriculumUnitStatistics'

const route = useRoute()

const statistics = ref<CurriculumUnitStatisticsDto | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

const unitId = computed(() => Number(route.params.id))

onMounted(async () => {
  await loadStatistics()
})

const gradeDistribution = computed((): GradeDistributionDto => {
  if (!statistics.value?.finalGrades.length) {
    return { excellent: 0, veryGood: 0, good: 0, satisfactory: 0, insufficient: 0 }
  }
  
  const grades = statistics.value.finalGrades
  return {
    excellent: grades.filter(g => g >= 18).length,
    veryGood: grades.filter(g => g >= 16 && g < 18).length,
    good: grades.filter(g => g >= 14 && g < 16).length,
    satisfactory: grades.filter(g => g >= 10 && g < 14).length,
    insufficient: grades.filter(g => g < 10).length
  }
})

const overallAverage = computed(() => {
  if (!statistics.value?.finalGrades.length) return 'N/A'
  const sum = statistics.value.finalGrades.reduce((acc, grade) => acc + grade, 0)
  return (sum / statistics.value.finalGrades.length).toFixed(1)
})

const passingRate = computed(() => {
  if (!statistics.value?.finalGrades.length) return '0'
  const total = statistics.value.finalGrades.length
  const passing = statistics.value.finalGrades.filter(grade => grade >= 10).length
  return ((passing / total) * 100).toFixed(1)
})

async function loadStatistics() {
  try {
    loading.value = true
    error.value = null
    statistics.value = await RemoteService.getCurriculumUnitStatistics(unitId.value)
  } catch (err: any) {
    console.error('Failed to load unit statistics:', err)
    error.value = err.message || 'Erro ao carregar estatísticas'
  } finally {
    loading.value = false
  }
}

function getGradePercentage(count: number): number {
  if (!statistics.value?.finalGrades.length) return 0
  return (count / statistics.value.finalGrades.length) * 100
}

function getRevisionPercentage(count: number): number {
  if (!statistics.value?.revisionStatistics.totalSubmitted) return 0
  return (count / statistics.value.revisionStatistics.totalSubmitted) * 100
}

function getApprovalRate(): string {
  if (!statistics.value?.revisionStatistics.totalSubmitted) return '0'
  const rate = (statistics.value.revisionStatistics.totalApproved / statistics.value.revisionStatistics.totalSubmitted) * 100
  return rate.toFixed(1)
}

function getDenialRate(): string {
  if (!statistics.value?.revisionStatistics.totalSubmitted) return '0'
  const rate = (statistics.value.revisionStatistics.totalDenied / statistics.value.revisionStatistics.totalSubmitted) * 100
  return rate.toFixed(1)
}

function getAssessmentIcon(type: string): string {
  return type === 'PROJECT' ? 'mdi-folder-outline' : 'mdi-file-document'
}

function getAssessmentColor(type: string): string {
  return type === 'PROJECT' ? 'orange' : 'blue'
}

function getAssessmentLabel(type: string): string {
  return type === 'PROJECT' ? 'Projeto' : 'Teste'
}

function getCompletionPercentage(assessment: AssessmentAverageDto): number {
  if (assessment.totalStudents === 0) return 100
  return (assessment.totalGraded / assessment.totalStudents) * 100
}

function getCompletionChipColor(assessment: AssessmentAverageDto): string {
  const percentage = getCompletionPercentage(assessment)
  if (percentage === 100) return 'success'
  if (percentage >= 75) return 'info'
  if (percentage >= 50) return 'warning'
  return 'error'
}
</script>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>