<!-- filepath: /home/paulo/DEI/candidatura-projeto/projeto-dei/src/frontend/src/views/people/assistants/AssistantGradingTasks.vue -->
<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold mb-2">Tarefas de Correção</h1>
          <p class="text-h6 text-medium-emphasis">Gerir avaliações e prazos de correção</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar tarefas de correção...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- No Tasks State -->
        <v-card v-else-if="gradingTasks.length === 0" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-icon icon="mdi-check-circle" size="64" color="success" class="mb-4"></v-icon>
            <h3 class="text-h5 mb-2">Todas as tarefas estão em dia!</h3>
            <p class="text-body-1 text-medium-emphasis">Não há avaliações pendentes de correção no momento.</p>
          </v-card-text>
        </v-card>

        <!-- Tasks Dashboard -->
        <div v-else>
          <!-- Summary Cards -->
          <v-row class="mb-6">
            <v-col cols="12" md="3">
              <v-card elevation="6" rounded="lg" color="error" variant="elevated">
                <v-card-text class="pa-4 text-center">
                  <v-icon icon="mdi-alert" size="32" color="white" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold text-white mb-1">{{ urgentTasks }}</h3>
                  <p class="text-body-2 text-white opacity-90 mb-0">Urgentes</p>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="3">
              <v-card elevation="6" rounded="lg" color="warning" variant="elevated">
                <v-card-text class="pa-4 text-center">
                  <v-icon icon="mdi-clock-alert" size="32" color="white" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold text-white mb-1">{{ upcomingTasks }}</h3>
                  <p class="text-body-2 text-white opacity-90 mb-0">Esta Semana</p>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="3">
              <v-card elevation="6" rounded="lg" color="success" variant="elevated">
                <v-card-text class="pa-4 text-center">
                  <v-icon icon="mdi-check-circle" size="32" color="white" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold text-white mb-1">{{ completedTasks }}</h3>
                  <p class="text-body-2 text-white opacity-90 mb-0">Concluídas</p>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="3">
              <v-card elevation="6" rounded="lg" color="info" variant="elevated">
                <v-card-text class="pa-4 text-center">
                  <v-icon icon="mdi-clipboard-list" size="32" color="white" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold text-white mb-1">{{ totalTasks }}</h3>
                  <p class="text-body-2 text-white opacity-90 mb-0">Total</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <!-- Filter Options -->
          <v-card elevation="4" rounded="lg" class="mb-6">
            <v-card-text class="pa-4">
              <v-row>
                <v-col cols="12" md="4">
                  <v-text-field
                    v-model="searchQuery"
                    label="Pesquisar avaliações..."
                    prepend-inner-icon="mdi-magnify"
                    variant="outlined"
                    clearable
                    hide-details
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="4">
                  <v-select
                    v-model="typeFilter"
                    :items="typeOptions"
                    label="Filtrar por tipo"
                    variant="outlined"
                    clearable
                    hide-details
                  ></v-select>
                </v-col>
                <v-col cols="12" md="4">
                  <v-select
                    v-model="priorityFilter"
                    :items="priorityOptions"
                    label="Filtrar por prioridade"
                    variant="outlined"
                    clearable
                    hide-details
                  ></v-select>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>

          <!-- Tasks Grid -->
          <v-row>
            <v-col 
              v-for="task in filteredTasks" 
              :key="`${task.assessmentType}-${task.assessmentId}`" 
              cols="12" 
              lg="6"
            >
              <v-card 
                class="task-card h-100" 
                elevation="4" 
                rounded="lg" 
                hover
                :color="getTaskCardColor(task)"
                variant="tonal"
              >
                <v-card-title class="pb-2">
                  <div class="d-flex align-center justify-space-between">
                    <div class="d-flex align-center">
                      <v-icon 
                        :icon="getAssessmentIcon(task.assessmentType)" 
                        :color="getAssessmentColor(task.assessmentType)"
                        size="24" 
                        class="mr-3"
                      ></v-icon>
                      <div>
                        <h3 class="text-h6 font-weight-bold line-clamp-1">
                          {{ task.assessmentName }}
                        </h3>
                        <p class="text-body-2 text-medium-emphasis mb-0">
                          {{ task.curriculumUnitCode }} - {{ task.curriculumUnitName }}
                        </p>
                      </div>
                    </div>
                    <div class="d-flex flex-column align-end">
                      <v-chip 
                        :color="getPriorityColor(task)"
                        variant="elevated"
                        size="small"
                        class="mb-1"
                      >
                        {{ getPriorityLabel(task) }}
                      </v-chip>
                      <v-chip 
                        :color="getAssessmentColor(task.assessmentType)"
                        variant="outlined"
                        size="small"
                      >
                        {{ getAssessmentLabel(task.assessmentType) }}
                      </v-chip>
                    </div>
                  </div>
                </v-card-title>
                
                <v-card-text>
                  <!-- Due Date Section -->
                  <div class="mb-3">
                    <div class="d-flex align-center mb-1">
                      <v-icon 
                        :icon="task.isPastDue ? 'mdi-calendar-alert' : 'mdi-calendar-clock'"
                        :color="task.isPastDue ? 'error' : 'primary'"
                        size="16" 
                        class="mr-2"
                      ></v-icon>
                      <span class="text-body-2 font-weight-medium">
                        {{ task.isPastDue ? 'Prazo Expirado' : 'Prazo' }}
                      </span>
                    </div>
                    <p class="text-body-1 mb-0" :class="task.isPastDue ? 'text-error' : ''">
                      {{ formatDueDate(task.dueDate) }}
                    </p>
                  </div>

                  <!-- Progress Section -->
                  <div class="mb-4">
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-body-1 font-weight-medium">Progresso da Correção</span>
                      <span class="text-h6 font-weight-bold">
                        {{ task.totalCount - task.pendingCount }}/{{ task.totalCount }}
                      </span>
                    </div>
                    <v-progress-linear
                      :model-value="getCompletionPercentage(task)"
                      :color="getProgressColor(task)"
                      height="8"
                      rounded
                      class="mb-2"
                    ></v-progress-linear>
                    <div class="d-flex justify-space-between text-caption text-medium-emphasis">
                      <span>{{ getCompletionPercentage(task).toFixed(1) }}% Concluído</span>
                      <span>{{ task.pendingCount }} Pendentes</span>
                    </div>
                  </div>

                  <!-- Status Alert -->
                  <v-alert 
                    v-if="task.pendingCount > 0 || task.isPastDue"
                    :color="getStatusAlertColor(task)"
                    variant="tonal"
                    density="compact"
                    class="mb-3"
                  >
                    <template v-slot:prepend>
                      <v-icon :icon="getStatusIcon(task)"></v-icon>
                    </template>
                    {{ getStatusMessage(task) }}
                  </v-alert>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteServices from '@/services/RemoteService'
import type { AssistantGradingTaskDto } from '@/models/AssistantViewsDto'

const router = useRouter()
const roleStore = useRoleStore()

const gradingTasks = ref<AssistantGradingTaskDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const typeFilter = ref<string | null>(null)
const priorityFilter = ref<string | null>(null)

const typeOptions = [
  { title: 'Projetos', value: 'PROJECT' },
  { title: 'Testes', value: 'TEST' }
]

const priorityOptions = [
  { title: 'Urgente', value: 'HIGH' },
  { title: 'Média', value: 'MEDIUM' },
  { title: 'Baixa', value: 'LOW' },
  { title: 'Concluída', value: 'COMPLETED' }
]

// Summary computed properties
const urgentTasks = computed(() => {
  return gradingTasks.value.filter(task => 
    task.isPastDue && task.pendingCount > 0
  ).length
})

const upcomingTasks = computed(() => {
  const now = new Date()
  const weekFromNow = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
  
  return gradingTasks.value.filter(task => {
    const dueDate = new Date(task.dueDate)
    return !task.isPastDue && dueDate <= weekFromNow && task.pendingCount > 0
  }).length
})

const completedTasks = computed(() => {
  return gradingTasks.value.filter(task => task.pendingCount === 0).length
})

const totalTasks = computed(() => gradingTasks.value.length)

const filteredTasks = computed(() => {
  let filtered = gradingTasks.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(task => 
      task.assessmentName.toLowerCase().includes(query) ||
      task.curriculumUnitCode.toLowerCase().includes(query) ||
      task.curriculumUnitName.toLowerCase().includes(query)
    )
  }

  // Filter by type
  if (typeFilter.value) {
    filtered = filtered.filter(task => task.assessmentType === typeFilter.value)
  }

  // Filter by priority
  if (priorityFilter.value) {
    filtered = filtered.filter(task => getPriorityLevel(task) === priorityFilter.value)
  }

  return filtered
})

onMounted(async () => {
  await fetchGradingTasks()
})

async function fetchGradingTasks() {
  try {
    loading.value = true
    error.value = null
    
    const assistantId = roleStore.getCurrentUserID
    if (!assistantId) {
      throw new Error('ID do assistente não disponível')
    }
    
    gradingTasks.value = await RemoteServices.getAssistantGradingTasks(assistantId)
    console.log('Grading tasks loaded:', gradingTasks.value)
  } catch (err: any) {
    console.error('Failed to fetch grading tasks:', err)
    error.value = err.message || 'Erro ao carregar tarefas de correção'
  } finally {
    loading.value = false
  }
}

function getCompletionPercentage(task: AssistantGradingTaskDto): number {
  if (task.totalCount === 0) return 100
  return ((task.totalCount - task.pendingCount) / task.totalCount) * 100
}

function getPriorityLevel(task: AssistantGradingTaskDto): string {
  if (task.isPastDue && task.pendingCount > 0) return 'HIGH'
  if (task.isPastDue && task.pendingCount === 0) return 'COMPLETED'
  
  const now = new Date()
  const dueDate = new Date(task.dueDate)
  const threeDaysFromNow = new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000)
  
  if (dueDate <= threeDaysFromNow && task.pendingCount > 0) return 'MEDIUM'
  return 'LOW'
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

function getPriorityColor(task: AssistantGradingTaskDto): string {
  const priority = getPriorityLevel(task)
  switch (priority) {
    case 'HIGH': return 'error'
    case 'MEDIUM': return 'warning'
    case 'COMPLETED': return 'success'
    default: return 'info'
  }
}

function getPriorityLabel(task: AssistantGradingTaskDto): string {
  const priority = getPriorityLevel(task)
  switch (priority) {
    case 'HIGH': return 'Urgente'
    case 'MEDIUM': return 'Média'
    case 'COMPLETED': return 'Concluída'
    default: return 'Baixa'
  }
}

function getTaskCardColor(task: AssistantGradingTaskDto): string {
  const priority = getPriorityLevel(task)
  switch (priority) {
    case 'HIGH': return 'error'
    case 'MEDIUM': return 'warning'
    case 'COMPLETED': return 'success'
    default: return 'info'
  }
}

function getProgressColor(task: AssistantGradingTaskDto): string {
  const percentage = getCompletionPercentage(task)
  if (percentage === 100) return 'success'
  if (percentage >= 75) return 'info'
  if (percentage >= 50) return 'warning'
  return 'error'
}

function getStatusAlertColor(task: AssistantGradingTaskDto): string {
  if (task.isPastDue && task.pendingCount > 0) return 'error'
  if (task.isPastDue && task.pendingCount === 0) return 'success'
  if (task.pendingCount > 0) return 'warning'
  return 'info'
}

function getStatusIcon(task: AssistantGradingTaskDto): string {
  if (task.isPastDue && task.pendingCount > 0) return 'mdi-alert'
  if (task.isPastDue && task.pendingCount === 0) return 'mdi-check-circle'
  if (task.pendingCount > 0) return 'mdi-clock-alert'
  return 'mdi-information'
}

function getStatusMessage(task: AssistantGradingTaskDto): string {
  if (task.isPastDue && task.pendingCount > 0) {
    const itemType = task.assessmentType === 'PROJECT' ? 'grupos' : 'alunos'
    return `Prazo expirado! ${task.pendingCount} ${itemType} aguardam correção urgente.`
  }
  if (task.isPastDue && task.pendingCount === 0) {
    return 'Correção concluída com sucesso!'
  }
  if (task.pendingCount > 0) {
    const itemType = task.assessmentType === 'PROJECT' ? 'grupos' : 'alunos'
    return `${task.pendingCount} ${itemType} aguardam correção.`
  }
  return 'Todas as correções estão em dia.'
}

function getActionButtonColor(task: AssistantGradingTaskDto): string {
  if (task.pendingCount === 0) return 'success'
  if (task.isPastDue) return 'error'
  return 'primary'
}

function getActionIcon(task: AssistantGradingTaskDto): string {
  if (task.pendingCount === 0) return 'mdi-eye'
  return 'mdi-pencil'
}

function getActionLabel(task: AssistantGradingTaskDto): string {
  if (task.pendingCount === 0) return 'Ver Detalhes'
  return 'Corrigir'
}

function formatDueDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('pt-PT', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}


</script>

<style scoped>
.task-card:hover {
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