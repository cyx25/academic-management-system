<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold text-primary mb-2">Progresso Académico</h1>
          <p class="text-h6 text-medium-emphasis">Estatísticas do seu desempenho</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar progresso...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- Progress Statistics -->
        <div v-else-if="progress">
          <!-- Main Statistics Cards -->
          <v-row class="mb-6">
            <!-- Average Grade -->
            <v-col cols="12" md="6" lg="3">
              <v-card 
                class="text-center pa-4" 
                elevation="8" 
                rounded="xl" 
                :color="getAverageGradeColor()"
                variant="elevated"
              >
                <v-card-text>
                  <v-icon 
                    icon="mdi-star" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ progress.averageGrade ? progress.averageGrade.toFixed(1) : '--' }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Média Geral</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Curriculum Units Progress -->
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
                    icon="mdi-school" 
                    size="48" 
                    color="white" 
                    class="mb-3"
                  ></v-icon>
                  <h2 class="text-h2 font-weight-bold text-white mb-2">
                    {{ progress.completedCurriculumUnits }}/{{ progress.totalCurriculumUnits }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">UCs Concluídas</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Projects Progress -->
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
                    {{ progress.completedProjects }}/{{ progress.totalProjects }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Projetos</p>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Tests Progress -->
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
                    {{ progress.completedTests }}/{{ progress.totalTests }}
                  </h2>
                  <p class="text-h6 text-white opacity-90 mb-0">Testes</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <!-- Detailed Progress Cards -->
          <v-row>
            <!-- Curriculum Units Progress -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-school" color="success" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Unidades Curriculares</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <div class="d-flex justify-space-between align-center mb-3">
                    <span class="text-body-1 font-weight-medium">Progresso Geral</span>
                    <span class="text-h6 font-weight-bold">
                      {{ getCurriculumUnitsPercentage() }}%
                    </span>
                  </div>
                  <v-progress-linear
                    :model-value="getCurriculumUnitsPercentage()"
                    color="success"
                    height="12"
                    rounded
                    class="mb-4"
                  ></v-progress-linear>
                  
                  <div class="d-flex justify-space-between text-body-2 text-medium-emphasis">
                    <span>{{ progress.completedCurriculumUnits }} Concluídas</span>
                    <span>{{ progress.totalCurriculumUnits - progress.completedCurriculumUnits }} Pendentes</span>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Assessments Progress -->
            <v-col cols="12" md="6">
              <v-card elevation="6" rounded="lg" class="h-100">
                <v-card-title class="d-flex align-center pa-4">
                  <v-icon icon="mdi-chart-pie" color="primary" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold">Avaliações</h3>
                </v-card-title>
                <v-card-text class="pa-4">
                  <!-- Projects Progress -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-folder-outline" size="16" class="mr-1"></v-icon>
                        Projetos
                      </span>
                      <span class="text-body-1 font-weight-bold">
                        {{ progress.completedProjects }}/{{ progress.totalProjects }}
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

                  <!-- Tests Progress -->
                  <div>
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">
                        <v-icon icon="mdi-file-document-outline" size="16" class="mr-1"></v-icon>
                        Testes
                      </span>
                      <span class="text-body-1 font-weight-bold">
                        {{ progress.completedTests }}/{{ progress.totalTests }}
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
import type ProgressDto from '@/models/ProgressDto'

const roleStore = useRoleStore()

const progress = ref<ProgressDto | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  await fetchProgress()
})

async function fetchProgress() {
  try {
    loading.value = true
    error.value = null
    
    const studentId = roleStore.getCurrentUserID
    if (!studentId) {
      throw new Error('ID do estudante não disponível')
    }
    
    progress.value = await RemoteServices.getStudentProgress(studentId)
    console.log('Progress loaded:', progress.value)
  } catch (err: any) {
    console.error('Failed to fetch progress:', err)
    error.value = err.message || 'Erro ao carregar progresso'
  } finally {
    loading.value = false
  }
}

function getCurriculumUnitsPercentage(): number {
  if (!progress.value || progress.value.totalCurriculumUnits === 0) return 0
  return Math.round((progress.value.completedCurriculumUnits / progress.value.totalCurriculumUnits) * 100)
}

function getProjectsPercentage(): number {
  if (!progress.value || progress.value.totalProjects === 0) return 0
  return Math.round((progress.value.completedProjects / progress.value.totalProjects) * 100)
}

function getTestsPercentage(): number {
  if (!progress.value || progress.value.totalTests === 0) return 0
  return Math.round((progress.value.completedTests / progress.value.totalTests) * 100)
}

function getAverageGradeColor(): string {
  if (!progress.value?.averageGrade) return 'grey'
  
  const grade = progress.value.averageGrade
  if (grade >= 16) return 'success'
  if (grade >= 14) return 'info'
  if (grade >= 10) return 'warning'
  return 'error'
}


</script>