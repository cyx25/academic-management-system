<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold text-primary mb-2">Perfil do Estudante</h1>
          <p class="text-h6 text-medium-emphasis">As suas unidades curriculares e notas</p>
        </div>

        <!-- Overall Grade Summary -->
        <v-card v-if="!loading && units.length > 0" class="mb-6" elevation="8" rounded="xl">
          <v-card-title class="text-center py-6">
            <v-icon icon="mdi-trophy" size="48" color="amber" class="mb-3"></v-icon>
            <h2 class="text-h4 font-weight-bold">Nota Final Geral</h2>
          </v-card-title>
          <v-card-text class="text-center pb-6">
            <div class="text-h2 font-weight-bold mb-2" :class="getGradeColorClass(finalGrade)">
              {{ finalGrade ? finalGrade.toFixed(1) : '--' }}/20
            </div>
            <v-chip 
              :color="getGradeColor(finalGrade || 0)" 
              variant="elevated" 
              size="large"
              class="font-weight-bold"
            >
              {{ getGradeLabel(finalGrade || 0) }}
            </v-chip>
          </v-card-text>
        </v-card>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar o seu perfil...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- No Units State -->
        <v-card v-else-if="units.length === 0" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-icon icon="mdi-school-outline" size="64" color="grey" class="mb-4"></v-icon>
            <h3 class="text-h5 mb-2">Nenhuma unidade curricular encontrada</h3>
            <p class="text-body-1 text-medium-emphasis">Ainda não está inscrito em nenhuma UC.</p>
          </v-card-text>
        </v-card>

        <!-- Curriculum Units Grid -->
        <div v-else>
          <h2 class="text-h4 mb-6 font-weight-bold">Unidades Curriculares</h2>
          <v-row>
            <v-col 
              v-for="unit in units" 
              :key="unit.id" 
              cols="12" 
              md="6" 
              lg="4"
            >
              <v-card 
                class="unit-card" 
                elevation="6" 
                rounded="lg" 
                hover
                @click="navigateToUnit(unit.id)"
                style="cursor: pointer; transition: all 0.3s ease;"
              >
                <v-card-title class="pb-2">
                  <div class="d-flex align-center">
                    <v-icon icon="mdi-book-education" color="primary" class="mr-3"></v-icon>
                    <div class="flex-grow-1">
                      <h3 class="text-h6 font-weight-bold line-clamp-2">{{ unit.name }}</h3>
                    </div>
                  </div>
                </v-card-title>
                
                <v-card-text class="pt-2">
                  <!-- Grade Display -->
                  <div class="text-center">
                    <div v-if="unit.finalGrade !== null" class="mb-3">
                      <div class="text-h3 font-weight-bold mb-2" :class="getGradeColorClass(unit.finalGrade)">
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
                    <div v-else class="mb-3">
                      <div class="text-h4 text-medium-emphasis mb-2">--/20</div>
                      <v-chip variant="tonal" color="grey" size="small">
                        Em avaliação
                      </v-chip>
                    </div>
                  </div>
                </v-card-text>

                <v-card-actions class="pt-0">
                  <v-spacer></v-spacer>
                  <v-btn 
                    variant="text" 
                    color="primary" 
                    prepend-icon="mdi-arrow-right"
                    @click.stop="navigateToUnit(unit.id)"
                  >
                    Ver detalhes
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
import type UnitFinalGradeDto from '@/models/UnitFinalGradeDto'

const router = useRouter()
const roleStore = useRoleStore()

const units = ref<UnitFinalGradeDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const finalGrade = computed(() => {
  const gradedUnits = units.value.filter(unit => unit.finalGrade !== null)
  if (gradedUnits.length === 0) return null
  
  const sum = gradedUnits.reduce((acc, unit) => acc + (unit.finalGrade || 0), 0)
  return sum / gradedUnits.length
})

onMounted(async () => {
  await fetchProfile()
})

async function fetchProfile() {
  try {
    loading.value = true
    error.value = null
    
    const studentId = roleStore.getCurrentUserID
    if (!studentId) {
      throw new Error('ID do estudante não disponível')
    }
    
    units.value = await RemoteServices.getStudentProfile(studentId)
    console.log('Profile loaded:', units.value)
  } catch (err: any) {
    console.error('Failed to fetch profile:', err)
    error.value = err.message || 'Erro ao carregar perfil'
  } finally {
    loading.value = false
  }
}

function navigateToUnit(unitId: number) {
  router.push(`/curriculum-units/${unitId}`)
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
.unit-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.15) !important;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>