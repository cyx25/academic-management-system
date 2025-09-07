<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-h3 font-weight-bold mb-2">Meus Alunos</h1>
          <p class="text-h6 text-medium-emphasis">Estudantes das suas unidades curriculares</p>
        </div>

        <!-- Loading State -->
        <v-card v-if="loading" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-progress-circular indeterminate color="primary" size="64" class="mb-4"></v-progress-circular>
            <p class="text-h6">A carregar alunos...</p>
          </v-card-text>
        </v-card>

        <!-- Error State -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-6">
          <v-alert-title>Erro ao carregar dados</v-alert-title>
          {{ error }}
        </v-alert>

        <!-- No Students State -->
        <v-card v-else-if="students.length === 0" class="text-center py-8" elevation="4" rounded="lg">
          <v-card-text>
            <v-icon icon="mdi-account-school" size="64" color="grey" class="mb-4"></v-icon>
            <h3 class="text-h5 mb-2">Nenhum aluno encontrado</h3>
            <p class="text-body-1 text-medium-emphasis">Não há alunos inscritos nas suas unidades curriculares.</p>
          </v-card-text>
        </v-card>

        <!-- Students List -->
        <div v-else>
          <!-- Summary Card -->
          <v-card elevation="6" rounded="lg" color="blue" variant="elevated" class="mb-6">
            <v-card-text class="pa-6 text-center">
              <v-icon icon="mdi-account-group" size="48" color="white" class="mb-3"></v-icon>
              <h2 class="text-h4 font-weight-bold text-white mb-2">
                {{ students.length }} Alunos
              </h2>
              <p class="text-h6 text-white opacity-90 mb-0">
                Distribuídos pelas suas unidades curriculares
              </p>
            </v-card-text>
          </v-card>

          <!-- Search and Filter -->
          <v-card elevation="4" rounded="lg" class="mb-6">
            <v-card-text class="pa-4">
              <v-text-field
                v-model="searchQuery"
                label="Pesquisar alunos..."
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                clearable
                hide-details
              ></v-text-field>
            </v-card-text>
          </v-card>

          <!-- Students Grid -->
          <v-row>
            <v-col 
              v-for="studentData in filteredStudents" 
              :key="studentData.student.id" 
              cols="12" 
              md="6" 
              lg="4"
            >
              <v-card 
                class="student-card h-100" 
                elevation="4" 
                rounded="lg" 
                hover
              >
                <v-card-title class="pb-2">
                  <div class="d-flex align-center">
                    <v-avatar color="blue" size="40" class="mr-3">
                      <span class="text-white font-weight-bold">
                        {{ getStudentInitials(studentData.student.name) }}
                      </span>
                    </v-avatar>
                    <div class="flex-grow-1">
                      <h3 class="text-h6 font-weight-bold line-clamp-1">
                        {{ studentData.student.name }}
                      </h3>
                      <p class="text-body-2 text-medium-emphasis mb-0">
                        {{ studentData.student.istId }}
                      </p>
                    </div>
                  </div>
                </v-card-title>
                
                <v-card-text>
                  <!-- Email -->
                  <div class="d-flex align-center mb-3">
                    <v-icon icon="mdi-email" color="blue" size="16" class="mr-2"></v-icon>
                    <span class="text-body-2 line-clamp-1">{{ studentData.student.email }}</span>
                  </div>

                  <!-- Curriculum Units -->
                  <div class="mb-3">
                    <div class="d-flex align-center mb-2">
                      <v-icon icon="mdi-book-education" color="blue" size="16" class="mr-2"></v-icon>
                      <span class="text-body-2 font-weight-medium">Unidades Curriculares</span>
                    </div>
                    <div class="d-flex flex-wrap gap-1">
                      <v-chip 
                        v-for="unitCode in studentData.curriculumUnitCodes" 
                        :key="unitCode"
                        size="small"
                        variant="tonal"
                        color="primary"
                      >
                        {{ unitCode }}
                      </v-chip>
                    </div>
                  </div>
                </v-card-text>

               
              </v-card>
            </v-col>
          </v-row>
        </div>
      </v-col>
    </v-row>

    <!-- Student Details Dialog -->
    <StudentDashboardDialog
      v-model="studentDialog"
      :student="selectedStudent"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoleStore } from '@/stores/role'
import RemoteServices from '@/services/RemoteService'
import type { TeacherStudentDto } from '@/models/TeacherViewsDto'
import type PersonDto from '@/models/PersonDto'
import StudentDashboardDialog from '@/components/StudentDashboardDialog.vue'

const roleStore = useRoleStore()

const students = ref<TeacherStudentDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const studentDialog = ref(false)
const selectedStudent = ref<PersonDto | null>(null)

const filteredStudents = computed(() => {
  if (!searchQuery.value) return students.value
  
  const query = searchQuery.value.toLowerCase()
  return students.value.filter(studentData => 
    studentData.student.name.toLowerCase().includes(query) ||
    studentData.student.istId.toLowerCase().includes(query) ||
    studentData.student.email.toLowerCase().includes(query) ||
    studentData.curriculumUnitCodes.some(code => code.toLowerCase().includes(query))
  )
})

onMounted(async () => {
  await fetchStudents()
})

async function fetchStudents() {
  try {
    loading.value = true
    error.value = null
    
    const teacherId = roleStore.getCurrentUserID
    if (!teacherId) {
      throw new Error('ID do professor não disponível')
    }
    
    students.value = await RemoteServices.getTeacherStudents(teacherId)
    console.log('Students loaded:', students.value)
  } catch (err: any) {
    console.error('Failed to fetch students:', err)
    error.value = err.message || 'Erro ao carregar alunos'
  } finally {
    loading.value = false
  }
}

function getStudentInitials(name: string): string {
  return name
    .split(' ')
    .map(part => part.charAt(0))
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

function viewStudentDetails(student: PersonDto) {
  selectedStudent.value = student
  studentDialog.value = true
}
</script>

<style scoped>
.student-card:hover {
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