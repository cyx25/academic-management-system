<template>
  <v-container fluid class="pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <v-btn 
            icon="mdi-arrow-left" 
            variant="text" 
            size="large"
            color="primary"
            @click="$router.go(-1)"
            class="mr-4"
          >
          </v-btn>
          <div>
            <h1 class="text-h4 font-weight-bold text-primary mb-1">Testes dos Estudantes</h1>
            <p v-if="test" class="text-h6 text-grey-darken-1 mb-0">{{ test.title }}</p>
          </div>
        </div>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary" class="mb-4"></v-progress-circular>
      <p class="text-h5 font-weight-medium text-grey-darken-1">A carregar testes dos estudantes...</p>
    </div>

    <!-- Student Tests Grid -->
    <v-row v-else-if="studentTests.length > 0">
      <v-col v-for="studentTest in studentTests" :key="studentTest.id" cols="12" md="6" lg="4">
        <v-card elevation="4" class="student-test-card">
          <v-card-title class="pa-4">
            <div class="d-flex justify-space-between align-center w-100">
              <div class="d-flex align-center">
                <v-avatar color="blue" size="40" class="mr-3">
                  <v-icon icon="mdi-account" color="white"></v-icon>
                </v-avatar>
                <div>
                  <h3 class="text-h6 font-weight-bold mb-1">{{ studentTest.studentName }}</h3>
                
                </div>
              </div>
              <v-chip 
                :color="studentTest.grade !== null ? 'success' : 'grey-lighten-1'" 
                size="large"
                class="font-weight-bold"
              >
                {{ studentTest.grade !== null ? `${studentTest.grade}/20` : 'Not graded' }}
              </v-chip>
            </div>
          </v-card-title>

          <!-- Grading Section -->
          <v-card-text class="pa-4">
            <div class="grading-section">
              <div class="d-flex align-center gap-3">
                <v-text-field
                  v-model.number="grades[studentTest.id]"
                  label="Nota (0-20)"
                  type="number"
                  min="0"
                  max="20"
                  step="0.5"
                  density="comfortable"
                  variant="outlined"
                  hide-details
                  class="grade-input"
                >
                  <template v-slot:append-inner>
                    <span class="text-grey-darken-1">/20</span>
                  </template>
                </v-text-field>
                
                <v-btn 
                  color="primary" 
                  @click="gradeStudent(studentTest.id)"
                  :disabled="isInvalidGrade(grades[studentTest.id])"
                  :loading="gradingLoading[studentTest.id]"
                >
                  NOTA
                </v-btn>
              </div>
            </div>

            <!-- Graded Teacher Info -->
            <div v-if="studentTest.gradedTeacherName" class="mt-3">
              <v-divider class="mb-2"></v-divider>
              <div class="d-flex align-center">
                <v-icon icon="mdi-account-check" size="small" color="success" class="mr-2"></v-icon>
                <span class="text-body-2 text-success">
                  Avaliado por: {{ studentTest.gradedTeacherName }}
                </span>
              </div>
            </div>

            <!-- Pending Revision Info -->
            <div v-if="studentTest.pendingRevision" class="mt-3">
              <v-alert type="warning" variant="tonal" density="compact">
                <div class="d-flex align-center">
                  <v-icon icon="mdi-alert" class="mr-2"></v-icon>
                  <span class="font-weight-medium">Revisão solicitada pelo estudante</span>
                </div>
              </v-alert>
              
              <v-btn
                v-if="roleStore.isMainTeacher"
                color="orange"
                variant="outlined"
                size="small"
                class="mt-2"
                @click="viewRevisions(studentTest.id)"
              >
                Revisar Revisões
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Empty State -->
    <div v-else class="text-center py-12">
      <v-alert type="info" variant="tonal" max-width="500" class="mx-auto">
        <div class="text-center">
          <v-icon icon="mdi-information" size="48" class="mb-3"></v-icon>
          <p class="text-h6 font-weight-bold mb-2">Sem estudantes encontrados</p>
          <p class="text-body-2 mb-0">Não há estudantes inscritos neste teste.</p>
        </div>
      </v-alert>
    </div>

    <!-- Revisions Dialog -->
    <RevisionDialog
      v-model="revisionsDialog"
      :student-test-id="selectedStudentTestId"
      @revision-processed="refreshStudentTests"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import RevisionDialog from './RevisionDialog.vue'

const route = useRoute()
const roleStore = useRoleStore()

const testeId = computed(() => Number(route.params.testId))
const unitId = computed(() => Number(route.params.id))

const test = ref<any>(null)
const studentTests = ref<any[]>([])
const grades = ref<Record<number, number | null>>({})
const gradingLoading = ref<Record<number, boolean>>({})
const loading = ref(true)
const revisionsDialog = ref(false)
const selectedStudentTestId = ref<number | null>(null)

onMounted(async () => {
  await refreshStudentTests()
})

async function refreshStudentTests() {
  try {
    loading.value = true
    
    // Fetch test details
    const allTests = await RemoteService.getTests(unitId.value)
    test.value = allTests.find(t => t.id === testeId.value)
    
    // Fetch student tests
    studentTests.value = await RemoteService.getStudentTests(testeId.value)
    
    // Initialize grades
    studentTests.value.forEach(st => {
      grades.value[st.id] = st.grade
      gradingLoading.value[st.id] = false
    })
  } catch (error) {
    console.error("Failed to load student tests:", error)
  } finally {
    loading.value = false
  }
}

function isInvalidGrade(grade: number | null): boolean {
  return grade === null || grade < 0 || grade > 20
}

async function gradeStudent(studentTestId: number) {
  const grade = grades.value[studentTestId]
  const teacherId = roleStore.getCurrentUserID

  if (isInvalidGrade(grade) || !teacherId) {
    return
  }

  try {
    gradingLoading.value[studentTestId] = true
    await RemoteService.gradeStudentTest(studentTestId, teacherId, grade!)
    
    // Refresh data
    await refreshStudentTests()
  } catch (error) {
    console.error("Failed to save grade:", error)
  } finally {
    gradingLoading.value[studentTestId] = false
  }
}

function viewRevisions(studentTestId: number) {
  selectedStudentTestId.value = studentTestId
  revisionsDialog.value = true
}
</script>

<style scoped>
.student-test-card {
  transition: all 0.3s ease;
}

.student-test-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1) !important;
}

.grade-input {
  max-width: 140px;
}

.grading-section {
  background-color: var(--v-surface-container-low);
  padding: 16px;
  border-radius: 8px;
  border: 1px solid var(--v-border-color, #e9ecef);
}
</style>