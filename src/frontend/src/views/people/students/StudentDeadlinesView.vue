<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold text-primary mb-2">Prazos de Entrega</h1>
          <p class="text-h6 text-medium-emphasis">Calendário de avaliações e projetos</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar prazos...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- No Deliveries State -->
        <v-card v-else-if="deliveries.length === 0" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-icon icon="mdi-calendar-blank" size="64" color="grey" class="mb-4"></v-icon>
            <h3 class="text-h5 mb-2">Nenhum prazo encontrado</h3>
            <p class="text-body-1 text-medium-emphasis">Não há avaliações agendadas.</p>
          </v-card-text>
        </v-card>

        <!-- Simplified Calendar View -->
        <div v-else>
          <!-- Conflicts Warning -->
          <v-alert 
            v-if="hasConflicts" 
            type="warning" 
            variant="tonal" 
            class="mb-6"
            icon="mdi-alert"
          >
            <v-alert-title>Conflitos Detectados!</v-alert-title>
            Existem múltiplas avaliações marcadas para o mesmo dia. Verifique os prazos abaixo.
          </v-alert>

          <!-- Calendar Timeline -->
          <v-card elevation="6" rounded="lg">
            <v-card-title class="d-flex align-center pa-4">
              <v-icon icon="mdi-calendar-month" color="primary" class="mr-3"></v-icon>
              <h2 class="text-h5 font-weight-bold">Calendário de Avaliações</h2>
            </v-card-title>
            
            <v-card-text class="pa-0">
              <v-timeline density="comfortable" class="pa-4">
                <v-timeline-item
                  v-for="(dayGroup, date) in groupedDeliveries"
                  :key="date"
                  :dot-color="getDayColor(dayGroup)"
                  size="large"
                >
                  <template v-slot:icon>
                    <v-icon 
                      :icon="dayGroup.length > 1 ? 'mdi-alert' : getAssessmentIcon(dayGroup[0].assessmentType)"
                      color="white"
                    ></v-icon>
                  </template>

                  <v-card 
                    class="mb-4" 
                    elevation="2" 
                    rounded="lg"
                    :color="dayGroup.length > 1 ? 'error-lighten-5' : 'surface'"
                  >
                    <v-card-title class="pb-2">
                      <div class="d-flex align-center justify-space-between w-100">
                        <h3 class="text-h6 font-weight-bold">{{ formatDate(date) }}</h3>
                        <v-chip 
                          v-if="dayGroup.length > 1" 
                          color="error" 
                          variant="elevated"
                          size="small"
                          prepend-icon="mdi-alert"
                        >
                          {{ dayGroup.length }} Conflitos
                        </v-chip>
                        <v-chip 
                          v-else
                          :color="getAssessmentColor(dayGroup[0].assessmentType)"
                          variant="tonal"
                          size="small"
                        >
                          {{ getAssessmentLabel(dayGroup[0].assessmentType) }}
                        </v-chip>
                      </div>
                    </v-card-title>

                    <v-card-text>
                      <div v-for="(delivery, index) in dayGroup" :key="index" class="mb-3">
                        <div class="d-flex align-center">
                          <v-icon 
                            :icon="getAssessmentIcon(delivery.assessmentType)" 
                            :color="getAssessmentColor(delivery.assessmentType)"
                            class="mr-3"
                          ></v-icon>
                          <div class="flex-grow-1">
                            <h4 class="text-subtitle-1 font-weight-medium">
                              {{ delivery.curriculumUnitName }}
                            </h4>
                            <p class="text-body-2 text-medium-emphasis mb-0">
                              {{ formatTime(delivery.submissionTime) }}
                            </p>
                          </div>
                          <v-chip 
                            :color="getAssessmentColor(delivery.assessmentType)"
                            variant="outlined"
                            size="small"
                          >
                            {{ getAssessmentLabel(delivery.assessmentType) }}
                          </v-chip>
                        </div>
                        <v-divider v-if="index < dayGroup.length - 1" class="mt-3"></v-divider>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-timeline-item>
              </v-timeline>
            </v-card-text>
          </v-card>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoleStore } from '@/stores/role'
import RemoteServices from '@/services/RemoteService'
import type AssessmentDeliveryDto from '@/models/AssessmentDeliveryDto'

const roleStore = useRoleStore()

const deliveries = ref<AssessmentDeliveryDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const groupedDeliveries = computed(() => {
  const grouped: { [date: string]: AssessmentDeliveryDto[] } = {}
  
  deliveries.value.forEach(delivery => {
    const date = new Date(delivery.submissionTime).toDateString()
    if (!grouped[date]) {
      grouped[date] = []
    }
    grouped[date].push(delivery)
  })
  
  // Sort dates
  const sortedEntries = Object.entries(grouped).sort(([a], [b]) => 
    new Date(a).getTime() - new Date(b).getTime()
  )
  
  return Object.fromEntries(sortedEntries)
})

const hasConflicts = computed(() => {
  return Object.values(groupedDeliveries.value).some(dayGroup => dayGroup.length > 1)
})

onMounted(async () => {
  await fetchDeliveries()
})

async function fetchDeliveries() {
  try {
    loading.value = true
    error.value = null
    
    const studentId = roleStore.getCurrentUserID
    if (!studentId) {
      throw new Error('ID do estudante não disponível')
    }
    
    deliveries.value = await RemoteServices.getStudentAssessmentDeliveries(studentId)
    console.log('Deliveries loaded:', deliveries.value)
  } catch (err: any) {
    console.error('Failed to fetch deliveries:', err)
    error.value = err.message || 'Erro ao carregar prazos'
  } finally {
    loading.value = false
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('pt-PT', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function formatTime(dateTimeString: string): string {
  const date = new Date(dateTimeString)
  return date.toLocaleTimeString('pt-PT', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getDayColor(dayGroup: AssessmentDeliveryDto[]): string {
  if (dayGroup.length > 1) return 'error'
  return getAssessmentColor(dayGroup[0].assessmentType)
}

function getAssessmentColor(type: string): string {
  switch (type) {
    case 'TEST': return 'blue'
    case 'PROJECT': return 'orange'
    default: return 'grey'
  }
}

function getAssessmentIcon(type: string): string {
  switch (type) {
    case 'TEST': return 'mdi-file-document'
    case 'PROJECT': return 'mdi-folder-outline'
    default: return 'mdi-help'
  }
}

function getAssessmentLabel(type: string): string {
  switch (type) {
    case 'TEST': return 'Teste'
    case 'PROJECT': return 'Projeto'
    default: return type
  }
}
</script>