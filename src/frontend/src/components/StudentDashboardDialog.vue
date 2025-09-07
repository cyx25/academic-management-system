<template>
  <v-dialog v-model="dialog" max-width="1200px" persistent scrollable>
    <v-card elevation="8" rounded="lg">
      <!-- Dialog Header -->
      <v-card-title class="d-flex align-center justify-space-between pa-4 bg-primary">
        <div class="d-flex align-center">
          <v-icon icon="mdi-account-school" color="white" size="32" class="mr-3"></v-icon>
          <div>
            <h2 class="text-h5 font-weight-bold text-white">
              Dashboard do Estudante
            </h2>
            <p class="text-body-2 text-white opacity-80 mb-0">
              {{ studentName }}
            </p>
          </div>
        </div>
        <v-btn 
          icon="mdi-close" 
          variant="text" 
          color="white"
          @click="closeDialog"
        ></v-btn>
      </v-card-title>

      <!-- Dialog Content -->
      <v-card-text class="pa-0" style="max-height: 80vh;">
        <v-container fluid class="pa-6">
          <!-- Loading State -->
          <div v-if="loading" class="text-center py-8">
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar dados do estudante...</p>
          </div>

          <!-- Error State -->
          <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
            <v-alert-title>Erro ao carregar dados</v-alert-title>
            {{ error }}
          </v-alert>

          <!-- Dashboard Content -->
          <div v-else>
            <!-- Progress Overview Cards -->
            <v-row class="mb-6" v-if="progress">
              <v-col cols="12" md="3">
                <v-card variant="tonal" color="primary" class="text-center pa-4">
                  <v-icon icon="mdi-star" size="32" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold">
                    {{ progress.averageGrade ? progress.averageGrade.toFixed(1) : '--' }}
                  </h3>
                  <p class="text-body-2">Média Geral</p>
                </v-card>
              </v-col>
              <v-col cols="12" md="3">
                <v-card variant="tonal" color="success" class="text-center pa-4">
                  <v-icon icon="mdi-school" size="32" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold">
                    {{ progress.completedCurriculumUnits }}/{{ progress.totalCurriculumUnits }}
                  </h3>
                  <p class="text-body-2">UCs Concluídas</p>
                </v-card>
              </v-col>
              <v-col cols="12" md="3">
                <v-card variant="tonal" color="orange" class="text-center pa-4">
                  <v-icon icon="mdi-folder-multiple" size="32" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold">
                    {{ progress.completedProjects }}/{{ progress.totalProjects }}
                  </h3>
                  <p class="text-body-2">Projetos</p>
                </v-card>
              </v-col>
              <v-col cols="12" md="3">
                <v-card variant="tonal" color="blue" class="text-center pa-4">
                  <v-icon icon="mdi-file-document" size="32" class="mb-2"></v-icon>
                  <h3 class="text-h4 font-weight-bold">
                    {{ progress.completedTests }}/{{ progress.totalTests }}
                  </h3>
                  <p class="text-body-2">Testes</p>
                </v-card>
              </v-col>
            </v-row>

            <!-- Student Profile - Curriculum Units -->
            <v-card elevation="4" rounded="lg" class="mb-6">
              <v-card-title class="d-flex align-center pa-4">
                <v-icon icon="mdi-book-education" color="primary" class="mr-3"></v-icon>
                <h3 class="text-h5 font-weight-bold">Unidades Curriculares</h3>
              </v-card-title>
              
              <v-card-text v-if="units.length === 0" class="text-center py-8">
                <v-icon icon="mdi-school-outline" size="64" color="grey" class="mb-4"></v-icon>
                <h4 class="text-h6 mb-2">Nenhuma unidade curricular encontrada</h4>
                <p class="text-body-2 text-medium-emphasis">O estudante ainda não está inscrito em nenhuma UC.</p>
              </v-card-text>

              <v-card-text v-else class="pa-4">
                <v-row>
                  <v-col 
                    v-for="unit in units" 
                    :key="unit.id" 
                    cols="12" 
                    md="6" 
                    lg="4"
                  >
                    <v-card variant="outlined" class="h-100">
                      <v-card-title class="pb-2">
                        <div class="d-flex align-center">
                          <v-icon icon="mdi-book" color="primary" class="mr-3"></v-icon>
                          <h4 class="text-subtitle-1 font-weight-bold line-clamp-2">
                            {{ unit.name }}
                          </h4>
                        </div>
                      </v-card-title>
                      
                      <v-card-text class="text-center">
                        <div v-if="unit.finalGrade !== null">
                          <div class="text-h4 font-weight-bold mb-2" :class="getGradeColorClass(unit.finalGrade)">
                            {{ unit.finalGrade }}/20
                          </div>
                          <v-chip 
                            :color="getGradeColor(unit.finalGrade)" 
                            variant="elevated"
                            size="small"
                            class="font-weight-bold"
                          >
                            {{ getGradeLabel(unit.finalGrade) }}
                          </v-chip>
                        </div>
                        <div v-else>
                          <div class="text-h5 text-medium-emphasis mb-2">--/20</div>
                          <v-chip variant="tonal" color="grey" size="small">
                            Em avaliação
                          </v-chip>
                        </div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </div>
        </v-container>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import RemoteServices from '@/services/RemoteService'
import type UnitFinalGradeDto from '@/models/UnitFinalGradeDto'
import type ProgressDto from '@/models/ProgressDto'
import type PersonDto from '@/models/PersonDto'

// Props and Events
interface Props {
  modelValue: boolean
  student: PersonDto | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

// Reactive Data
const dialog = ref(props.modelValue)
const loading = ref(false)
const error = ref<string | null>(null)
const units = ref<UnitFinalGradeDto[]>([])
const progress = ref<ProgressDto | null>(null)

// Computed
const studentName = ref('')

// Watchers
watch(() => props.modelValue, (newValue) => {
  dialog.value = newValue
  if (newValue && props.student) {
    studentName.value = props.student.name
    fetchStudentData()
  }
})

watch(dialog, (newValue) => {
  emit('update:modelValue', newValue)
  if (!newValue) {
    resetData()
  }
})

// Methods
async function fetchStudentData() {
  if (!props.student?.id) return
  
  try {
    loading.value = true
    error.value = null
    
    const [profileData, progressData] = await Promise.all([
      RemoteServices.getStudentProfile(props.student.id),
      RemoteServices.getStudentProgress(props.student.id)
    ])
    
    units.value = profileData
    progress.value = progressData
    
    console.log('Student data loaded:', { units: units.value, progress: progress.value })
  } catch (err: any) {
    console.error('Failed to fetch student data:', err)
    error.value = err.message || 'Erro ao carregar dados do estudante'
  } finally {
    loading.value = false
  }
}

function resetData() {
  units.value = []
  progress.value = null
  error.value = null
  loading.value = false
  studentName.value = ''
}

function closeDialog() {
  dialog.value = false
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
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>