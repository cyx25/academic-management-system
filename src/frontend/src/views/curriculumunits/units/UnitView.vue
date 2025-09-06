<template>
  <v-container fluid class="pa-6">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <p class="text-h6 mt-4">Loading curriculum unit...</p>
    </div>

    <!-- Unit Information -->
    <div v-else-if="curriculumUnit">
      <!-- Header Section -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-card class="pa-6 mb-4" elevation="2">
            <div class="d-flex align-center mb-4">
              <v-avatar color="primary" size="64" class="mr-4">
                <v-icon icon="mdi-school" size="32"></v-icon>
              </v-avatar>
              <div>
                <h1 class="text-h3 font-weight-bold text-primary mb-2">
                  {{ curriculumUnit.name }}
                </h1>
                <p class="text-h6 text-medium-emphasis mb-1">
                  {{ curriculumUnit.course?.name }}
                </p>
                <v-chip color="primary" variant="tonal" size="big" >
                  {{ curriculumUnit.ects }} ECTS
                </v-chip>
              </div>
            </div>
            
            <v-divider class="my-4"></v-divider>
            
            <div class="text-body-1">
              <p v-if="curriculumUnit.description">
                {{ curriculumUnit.description }}
              </p>
              <p v-else class="text-medium-emphasis font-italic">
                Ano Letivo 25/26
              </p>
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Quick Actions -->
      <v-row class="mb-6">
        <v-col cols="12">
          <h2 class="text-h5 font-weight-bold mb-4">Quick Actions</h2>
          <v-row>
            <!-- Tests Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'TestsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-clipboard-check" size="48" color="primary" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Tests</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'View your test results' : 'Manage tests and grading' }}
                </p>
              </v-card>
            </v-col>

            <!-- Projects Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'ProjectsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-folder-outline" size="48" color="secondary" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Projects</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'Submit your assignments' : 'Manage project assignments' }}
                </p>
              </v-card>
            </v-col>

            <!-- Materials Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'MaterialsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-book-open-outline" size="48" color="success" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Materials</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'Access course materials' : 'Organize learning resources' }}
                </p>
              </v-card>
            </v-col>

            <!-- Personnel Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'PersonnelView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-account-group" size="48" color="warning" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Personnel</h3>
                <p class="text-body-2 text-medium-emphasis">
                  View teachers and enrolled students
                </p>
              </v-card>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <!-- Statistics Overview (if student) -->
      <v-row v-if="roleStore.isStudent" class="mb-6">
        <v-col cols="12">
          <h2 class="text-h5 font-weight-bold mb-4">Your Progress</h2>
          <v-row>
            <v-col cols="12" md="4">
              <v-card class="pa-4 text-center" elevation="2">
                <v-icon icon="mdi-star" size="32" color="success" class="mb-2"></v-icon>
                <h3 class="text-h6 font-weight-bold">Final Grade</h3>
                <p class="text-h4 font-weight-bold text-success">
                  {{ studentGrade || 'N/A' }}
                </p>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card class="pa-4 text-center" elevation="2">
                <v-icon icon="mdi-clipboard-check" size="32" color="primary" class="mb-2"></v-icon>
                <h3 class="text-h6 font-weight-bold">Tests Completed</h3>
                <p class="text-h4 font-weight-bold text-primary">
                  {{ completedTests }}
                </p>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card class="pa-4 text-center" elevation="2">
                <v-icon icon="mdi-folder" size="32" color="secondary" class="mb-2"></v-icon>
                <h3 class="text-h6 font-weight-bold">Projects Submitted</h3>
                <p class="text-h4 font-weight-bold text-secondary">
                  {{ submittedProjects }}
                </p>
              </v-card>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <!-- Recent Activity (if teacher) -->
      <v-row v-if="roleStore.isTeacher">
        <v-col cols="12">
          <h2 class="text-h5 font-weight-bold mb-4">Recent Activity</h2>
          <v-card elevation="2">
            <v-card-text>
              <div class="d-flex align-center">
                <v-icon icon="mdi-information" color="info" class="mr-3"></v-icon>
                <span>Activity tracking and recent changes will be displayed here.</span>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>

    <!-- Error State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="error" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-alert-circle" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">Failed to Load Unit</h3>
          <p class="text-body-1">
            Unable to load curriculum unit information. Please try again.
          </p>
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'

const route = useRoute()
const roleStore = useRoleStore()

const curriculumUnit = ref<CurriculumUnitDto | null>(null)
const loading = ref(true)
const studentGrade = ref<string>('--')
const completedTests = ref<number>(0)
const submittedProjects = ref<number>(0)

const unitId = computed(() => Number(route.params.id))

onMounted(async () => {
  await loadUnitData()
})

async function loadUnitData() {
  try {
    loading.value = true
    
    // Load curriculum unit information
    curriculumUnit.value = await RemoteService.getCurriculumUnit(unitId.value)
    
    // Load student-specific data if student
    if (roleStore.isStudent && roleStore.getCurrentUserID) {
      await loadStudentData()
    }
  } catch (error) {
    console.error('Failed to load unit data:', error)
  } finally {
    loading.value = false
  }
}

async function loadStudentData() {
  try {
    // These methods should be implemented in your RemoteService
    // For now, we'll use placeholder values
    studentGrade.value = 'TBD'
    completedTests.value = 0
    submittedProjects.value = 0
    
    // TODO: Implement actual data fetching
    // const tests = await RemoteService.getStudentTestsForStudent(unitId.value, roleStore.getCurrentUserID)
    // const projects = await RemoteService.getStudentProjectsForStudent(unitId.value, roleStore.getCurrentUserID)
    // completedTests.value = tests.filter(t => t.grade !== null).length
    // submittedProjects.value = projects.filter(p => p.submitted).length
  } catch (error) {
    console.error('Failed to load student data:', error)
  }
}
</script>

<style scoped>
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.hover-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.hover-card:hover .v-icon {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}
</style>