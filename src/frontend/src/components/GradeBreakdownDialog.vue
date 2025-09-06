<template>
  <v-dialog v-model="dialog" max-width="700px" persistent>
    <v-card elevation="12" class="grade-dialog">
      <!-- Header -->
      <v-card-title class="pa-0">
        <div class="grade-header pa-6">
          <div class="d-flex align-center justify-space-between w-100">
            <div class="d-flex align-center">
              <v-avatar color="primary" size="48" class="mr-4">
                <v-icon icon="mdi-calculator" color="white" size="24"></v-icon>
              </v-avatar>
              <div>
                <h2 class="text-h5 font-weight-bold mb-1">Breakdown de Nota</h2>
                <p class="text-body-2 text-medium-emphasis mb-0">
                  {{ breakdown?.studentName || 'Carregando...' }}
                </p>
              </div>
            </div>
            <div v-if="breakdown?.finalGrade !== null && breakdown?.finalGrade !== undefined" class="text-center">
              <div class="final-grade-badge" :class="getGradeColorClass(breakdown.finalGrade)">
                {{ breakdown.finalGrade }}
              </div>
              <p class="text-caption mt-1">{{ getGradeLabel(breakdown.finalGrade) }}</p>
            </div>
          </div>
        </div>
      </v-card-title>

      <!-- Loading -->
      <v-card-text v-if="loading" class="text-center py-8">
        <v-progress-circular indeterminate size="48" color="primary"></v-progress-circular>
        <p class="mt-4 text-body-1">Carregando...</p>
      </v-card-text>

      <!-- Content -->
      <v-card-text v-else-if="breakdown" class="pa-6">
        <!-- Quick Stats -->
        <div class="stats-row mb-6">
          <div class="stat-item">
            <div class="stat-number">{{ breakdown.assessments?.length || 0 }}</div>
            <div class="stat-label">Avaliações</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ breakdown.totalWeight || 0 }}%</div>
            <div class="stat-label">Peso Total</div>
          </div>
          <div class="stat-item">
            <div class="stat-number" :class="breakdown.isComplete ? 'text-success' : 'text-warning'">
              {{ breakdown.isComplete ? '✓' : '⏳' }}
            </div>
            <div class="stat-label">{{ breakdown.isComplete ? 'Completo' : 'Em Curso' }}</div>
          </div>
        </div>

        <!-- Assignments List -->
        <div class="assignments-list" v-if="breakdown.assessments && breakdown.assessments.length > 0">
          <h3 class="text-h6 font-weight-bold mb-4">Detalhes das Avaliações</h3>
          
          <div v-for="assessment in breakdown.assessments" :key="assessment.name" class="assignment-card mb-3">
            <div class="d-flex align-center justify-space-between">
              <div class="d-flex align-center flex-grow-1">
                <v-chip 
                  :color="assessment.type === 'TEST' ? 'blue' : 'orange'" 
                  size="small" 
                  variant="tonal"
                  class="mr-3 flex-shrink-0"
                >
                  {{ assessment.type === 'TEST' ? 'Teste' : 'Projeto' }}
                </v-chip>
                <div class="flex-grow-1">
                  <p class="font-weight-medium mb-1">{{ assessment.name }}</p>
                  <p class="text-caption text-medium-emphasis">Peso: {{ assessment.weight }}%</p>
                </div>
              </div>
              
              <div class="text-right">
                <div v-if="assessment.grade !== null && assessment.grade !== undefined" class="grade-display">
                  <span class="grade-value" :class="getGradeColorClass(assessment.grade)">
                    {{ assessment.grade }}
                  </span>
                  <span class="grade-max">/20</span>
                  <p class="text-caption mt-1">
                    {{ (assessment.grade * assessment.weight / 100).toFixed(1) }} pts
                  </p>
                </div>
                <div v-else class="not-graded">
                  <span class="text-medium-emphasis">Não avaliado</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- No Assessments -->
        <div v-else class="text-center py-6">
          <v-icon icon="mdi-clipboard-off" size="48" color="grey" class="mb-3"></v-icon>
          <p class="text-body-1 text-medium-emphasis">Nenhuma avaliação encontrada</p>
        </div>

        <!-- Final Result -->
        <div v-if="breakdown.isComplete && breakdown.finalGrade !== null && breakdown.finalGrade !== undefined" class="final-result mt-6">
          <v-card variant="tonal" :color="getGradeColor(breakdown.finalGrade)" class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="d-flex align-center">
                <v-icon 
                  :icon="breakdown.finalGrade >= 10 ? 'mdi-check-circle' : 'mdi-close-circle'" 
                  size="32" 
                  :color="getGradeColor(breakdown.finalGrade)"
                  class="mr-3"
                ></v-icon>
                <div>
                  <h4 class="text-h6 font-weight-bold">Resultado Final</h4>
                  <p class="text-body-2">{{ breakdown.finalGrade >= 10 ? 'Aprovado' : 'Reprovado' }}</p>
                </div>
              </div>
              <div class="final-grade-large" :class="getGradeColorClass(breakdown.finalGrade)">
                {{ breakdown.finalGrade }}<span class="grade-max-large">/20</span>
              </div>
            </div>
          </v-card>
        </div>

        <!-- Incomplete Notice -->
        <v-alert v-else type="info" variant="tonal" class="mt-6">
          <p class="mb-0">
            <strong>Avaliação em andamento.</strong> A nota final será calculada quando todas as avaliações estiverem concluídas.
          </p>
        </v-alert>
      </v-card-text>

      <!-- Error State -->
      <v-card-text v-else class="text-center py-8">
        <v-icon icon="mdi-alert-circle" size="48" color="error" class="mb-3"></v-icon>
        <p class="text-body-1">Erro ao carregar dados</p>
      </v-card-text>

      <!-- Actions -->
      <v-card-actions class="pa-6">
        <v-spacer></v-spacer>
        <v-btn variant="text" @click="close">Fechar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import RemoteService from '@/services/RemoteService'
import type FinalGradeBreakdownDto from '@/models/FinalGradeBreakdownDto'

interface Props {
  modelValue: boolean
  studentId: number | null
  curriculumUnitId: number | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const breakdown = ref<FinalGradeBreakdownDto | null>(null)
const loading = ref(false)

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

watch(dialog, async (newValue) => {
  if (newValue && props.studentId && props.curriculumUnitId) {
    await fetchBreakdown()
  }
})

async function fetchBreakdown() {
  if (!props.studentId || !props.curriculumUnitId) return
  
  try {
    loading.value = true
    const data = await RemoteService.getFinalGradeBreakdown(
      props.studentId, 
      props.curriculumUnitId
    )
    
    // Validate data structure
    if (data && typeof data === 'object') {
      breakdown.value = data
      console.log('Grade breakdown loaded:', data)
    } else {
      console.error('Invalid data structure received:', data)
      breakdown.value = null
    }
  } catch (error) {
    console.error('Failed to fetch grade breakdown:', error)
    breakdown.value = null
  } finally {
    loading.value = false
  }
}

function getGradeColor(grade: number): string {
  if (grade >= 16) return 'success'
  if (grade >= 14) return 'info' 
  if (grade >= 10) return 'warning'
  return 'error'
}

function getGradeColorClass(grade: number): string {
  if (grade >= 16) return 'text-success'
  if (grade >= 14) return 'text-info'
  if (grade >= 10) return 'text-warning'
  return 'text-error'
}

function getGradeLabel(grade: number): string {
  if (grade >= 16) return 'Excelente'
  if (grade >= 14) return 'Bom'
  if (grade >= 10) return 'Suficiente'
  return 'Insuficiente'
}

function close() {
  dialog.value = false
  breakdown.value = null
}
</script>

<style scoped>
.grade-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.grade-header {
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)) 0%, rgb(var(--v-theme-secondary)) 100%);
  color: white;
}

.final-grade-badge {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.stats-row {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: rgb(var(--v-theme-primary));
}

.stat-label {
  font-size: 0.875rem;
  color: rgb(var(--v-theme-on-surface-variant));
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.assignment-card {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 12px;
  padding: 16px;
  border-left: 4px solid rgb(var(--v-theme-primary));
}

.grade-display {
  text-align: right;
}

.grade-value {
  font-size: 1.5rem;
  font-weight: bold;
}

.grade-max {
  font-size: 1rem;
  color: rgb(var(--v-theme-on-surface-variant));
}

.final-grade-large {
  font-size: 2.5rem;
  font-weight: bold;
}

.grade-max-large {
  font-size: 1.5rem;
  color: rgb(var(--v-theme-on-surface-variant));
}

.not-graded {
  padding: 8px 0;
}
</style>