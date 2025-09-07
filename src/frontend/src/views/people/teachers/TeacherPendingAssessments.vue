<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold mb-2">Correções Pendentes</h1>
          <p class="text-h6 text-medium-emphasis">Avaliações que aguardam correção</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar correções pendentes...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- No Pending Assessments State -->
        <v-card v-else-if="pendingAssessments.length === 0" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-icon icon="mdi-check-circle" size="64" color="success" class="mb-4"></v-icon>
            <h3 class="text-h5 mb-2">Todas as correções estão em dia!</h3>
            <p class="text-body-1 text-medium-emphasis">Não há avaliações pendentes de correção.</p>
          </v-card-text>
        </v-card>

        <!-- Pending Assessments List -->
        <div v-else>
          <!-- Summary Card -->
          <v-card elevation="6" rounded="lg" color="warning" variant="elevated" class="mb-6">
            <v-card-text class="pa-6 text-center">
              <v-icon icon="mdi-clipboard-alert" size="48" color="white" class="mb-3"></v-icon>
              <h2 class="text-h4 font-weight-bold text-white mb-2">
                {{ pendingAssessments.length }} Avaliações Pendentes
              </h2>
              <p class="text-h6 text-white opacity-90 mb-0">
                {{ totalPendingCount }} itens aguardam correção
              </p>
            </v-card-text>
          </v-card>

          <!-- Filter Options -->
          <v-card elevation="4" rounded="lg" class="mb-6">
            <v-card-text class="pa-4">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="searchQuery"
                    label="Pesquisar avaliações..."
                    prepend-inner-icon="mdi-magnify"
                    variant="outlined"
                    clearable
                    hide-details
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="typeFilter"
                    :items="typeOptions"
                    label="Filtrar por tipo"
                    variant="outlined"
                    clearable
                    hide-details
                  ></v-select>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>

          <!-- Assessments Grid -->
          <v-row>
            <v-col 
              v-for="assessment in filteredAssessments" 
              :key="`${assessment.assessmentType}-${assessment.assessmentId}`" 
              cols="12" 
              lg="6"
            >
              <v-card 
                class="assessment-card h-100" 
                elevation="4" 
                rounded="lg" 
                hover
                :color="getAssessmentCardColor(assessment)"
                variant="tonal"
              >
                <v-card-title class="pb-2">
                  <div class="d-flex align-center justify-space-between">
                    <div class="d-flex align-center">
                      <v-icon 
                        :icon="getAssessmentIcon(assessment.assessmentType)" 
                        :color="getAssessmentColor(assessment.assessmentType)"
                        size="24" 
                        class="mr-3"
                      ></v-icon>
                      <div>
                        <h3 class="text-h6 font-weight-bold line-clamp-1">
                          {{ assessment.assessmentName }}
                        </h3>
                        <p class="text-body-2 text-medium-emphasis mb-0">
                          {{ assessment.curriculumUnitCode }} - {{ assessment.curriculumUnitName }}
                        </p>
                      </div>
                    </div>
                    <v-chip 
                      :color="getAssessmentColor(assessment.assessmentType)"
                      variant="elevated"
                      size="small"
                    >
                      {{ getAssessmentLabel(assessment.assessmentType) }}
                    </v-chip>
                  </div>
                </v-card-title>
                
                <v-card-text>
                  <!-- Progress Section -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">Progresso da Correção</span>
                      <span class="text-h6 font-weight-bold">
                        {{ assessment.totalCount - assessment.pendingCount }}/{{ assessment.totalCount }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getCompletionPercentage(assessment)"
                      :color="getProgressColor(assessment)"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                    <div class="d-flex justify-space-between text-caption text-medium-emphasis">
                      <span>{{ getCompletionPercentage(assessment).toFixed(1) }}% Concluído</span>
                      <span>{{ assessment.pendingCount }} Pendentes</span>
                    </div>
                  </div>

                  <!-- Pending Count Alert -->
                  <v-alert 
                    v-if="assessment.pendingCount > 0"
                    :color="getPriorityColor(assessment.pendingCount)"
                    variant="tonal"
                    density="compact"
                    class="mb-3"
                  >
                    <template v-slot:prepend>
                      <v-icon :icon="getPriorityIcon(assessment.pendingCount)"></v-icon>
                    </template>
                    {{ getPriorityMessage(assessment.pendingCount, assessment.assessmentType) }}
                  </v-alert>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn 
                    variant="elevated" 
                    :color="getAssessmentColor(assessment.assessmentType)"
                    prepend-icon="mdi-pencil"
                    @click="openAssessment(assessment)"
                  >
                    Corrigir
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteServices from '@/services/RemoteService'
import type { PendingGradingDto } from '@/models/TeacherViewsDto'

const router = useRouter()
const roleStore = useRoleStore()

const pendingAssessments = ref<PendingGradingDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const typeFilter = ref<string | null>(null)

const typeOptions = [
  { title: 'Projetos', value: 'PROJECT' },
  { title: 'Testes', value: 'TEST' }
]

const totalPendingCount = computed(() => {
  return pendingAssessments.value.reduce((sum, assessment) => sum + assessment.pendingCount, 0)
})

const filteredAssessments = computed(() => {
  let filtered = pendingAssessments.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(assessment => 
      assessment.assessmentName.toLowerCase().includes(query) ||
      assessment.curriculumUnitCode.toLowerCase().includes(query) ||
      assessment.curriculumUnitName.toLowerCase().includes(query)
    )
  }

  // Filter by type
  if (typeFilter.value) {
    filtered = filtered.filter(assessment => assessment.assessmentType === typeFilter.value)
  }

  return filtered
})

onMounted(async () => {
  await fetchPendingAssessments()
})

async function fetchPendingAssessments() {
  try {
    loading.value = true
    error.value = null
    
    const teacherId = roleStore.getCurrentUserID
    if (!teacherId) {
      throw new Error('ID do professor não disponível')
    }
    
    pendingAssessments.value = await RemoteServices.getTeacherPendingGrading(teacherId)
    console.log('Pending assessments loaded:', pendingAssessments.value)
  } catch (err: any) {
    console.error('Failed to fetch pending assessments:', err)
    error.value = err.message || 'Erro ao carregar correções pendentes'
  } finally {
    loading.value = false
  }
}

function getCompletionPercentage(assessment: PendingGradingDto): number {
  if (assessment.totalCount === 0) return 100
  return ((assessment.totalCount - assessment.pendingCount) / assessment.totalCount) * 100
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

function getAssessmentCardColor(assessment: PendingGradingDto): string {
  if (assessment.pendingCount === 0) return 'success'
  if (assessment.pendingCount >= 10) return 'error'
  if (assessment.pendingCount >= 5) return 'warning'
  return 'info'
}

function getProgressColor(assessment: PendingGradingDto): string {
  const percentage = getCompletionPercentage(assessment)
  if (percentage >= 80) return 'success'
  if (percentage >= 50) return 'warning'
  return 'error'
}

function getPriorityColor(pendingCount: number): string {
  if (pendingCount >= 10) return 'error'
  if (pendingCount >= 5) return 'warning'
  return 'info'
}

function getPriorityIcon(pendingCount: number): string {
  if (pendingCount >= 10) return 'mdi-alert'
  if (pendingCount >= 5) return 'mdi-alert-outline'
  return 'mdi-information'
}

function getPriorityMessage(pendingCount: number, type: string): string {
  const itemType = type === 'PROJECT' ? 'grupos' : 'alunos'
  if (pendingCount >= 10) return `Urgente: ${pendingCount} ${itemType} aguardam correção!`
  if (pendingCount >= 5) return `Atenção: ${pendingCount} ${itemType} pendentes`
  return `${pendingCount} ${itemType} para corrigir`
}

function openAssessment(assessment: PendingGradingDto) {
  // Navigate to the appropriate assessment page
  const unitId = getCurrentUnitId(assessment) // You'll need to implement this
  
  if (assessment.assessmentType === 'PROJECT') {
    router.push(`/curriculum-units/${unitId}/projects`)
  } else {
    router.push(`/curriculum-units/${unitId}/tests`)
  }
}

function getCurrentUnitId(assessment: PendingGradingDto): number {
  // This would need to be implemented based on how you can get the unit ID
  // For now, return a placeholder
  return 1
}
</script>

<style scoped>
.assessment-card:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>