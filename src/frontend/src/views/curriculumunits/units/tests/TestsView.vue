<template>
  <v-container fluid class="pa-6">
    <!-- Header -->
    <v-row class="mb-4">
      <v-col cols="12" class="d-flex justify-space-between align-center">
        <div>
          <h1 class="text-h4 font-weight-bold text-primary mb-2">Tests</h1>
          <p class="text-body-1 text-medium-emphasis">
            {{ roleStore.isStudent ? 'View your test results and request revisions' : 'Manage curriculum unit tests and assessments' }}
          </p>
        </div>
        <div class="d-flex gap-2">
          <!-- Revisions Button for Teachers -->
          <v-btn
            v-if="roleStore.isTeacher"
            color="warning"
            variant="outlined"
            prepend-icon="mdi-comment-question-outline"
            @click="navigateToRevisions"
          >
            View Revisions
            <v-badge
              v-if="pendingRevisionsCount > 0"
              :content="pendingRevisionsCount"
              color="error"
              inline
            />
          </v-btn>
          
          <!-- Create Test Button -->
          <v-btn
            v-if="roleStore.isMainTeacher"
            color="primary"
            prepend-icon="mdi-plus"
            size="large"
            @click="openCreateDialog"
          >
            Create Test
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <p class="text-h6 mt-4">Loading tests...</p>
    </div>

    <!-- Tests Grid -->
    <v-row v-else-if="displayTests.length > 0">
      <v-col v-for="teste in displayTests" :key="teste.id" cols="12" sm="6" lg="4">
        <TestCard 
          :teste="teste" 
          :student-test-data="getStudentTestData(teste.id)"
          @update="refreshTests"
        />
      </v-col>
    </v-row>

    <!-- Empty State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="info" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-clipboard-check" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">No Tests Available</h3>
          <p class="text-body-1 mb-4">
            {{ roleStore.isMainTeacher 
              ? 'Create your first test to get started with assessments.' 
              : 'Tests will appear here when teachers create them.' 
            }}
          </p>
          <v-btn
            v-if="roleStore.isMainTeacher"
            color="primary"
            prepend-icon="mdi-plus"
            @click="openCreateDialog"
          >
            Create First Test
          </v-btn>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Create Test Dialog -->
    <CreateTestDialog
      v-model="createDialog"
      :unit-id="unitId"
      @test-created="onTestCreated"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import TestCard from './TestCard.vue'
import CreateTestDialog from './CreateTestDialog.vue'
import type TesteDto from '@/models/TesteDto'
import type StudentTesteDto from '@/models/StudentTesteDto'

const route = useRoute()
const router = useRouter()
const roleStore = useRoleStore()

const unitId = computed(() => Number(route.params.id))
const tests = ref<TesteDto[]>([])
const studentTests = ref<StudentTesteDto[]>([])
const loading = ref(true)
const createDialog = ref(false)
const pendingRevisionsCount = ref(0)

const displayTests = computed(() => {
  if (roleStore.isStudent) {
    const studentTestIds = studentTests.value.map(st => st.testeId)
    return tests.value.filter(test => test.id && studentTestIds.includes(test.id))
  }
  return tests.value
})

onMounted(async () => {
  await refreshTests()
  await loadPendingRevisionsCount()
})

async function refreshTests() {
  try {
    loading.value = true
    
    tests.value = await RemoteService.getTests(unitId.value)
    
    if (roleStore.isStudent && roleStore.getCurrentUserID) {
      studentTests.value = await RemoteService.getStudentTestsForStudent(
        unitId.value, 
        roleStore.getCurrentUserID
      )
    }
  } catch (error) {
    console.error('Failed to fetch tests:', error)
  } finally {
    loading.value = false
  }
}

async function loadPendingRevisionsCount() {
  if (roleStore.isTeacher) {
    try {
      const revisions = await RemoteService.getPendingRevisions(unitId.value)
      pendingRevisionsCount.value = revisions.length
    } catch (error) {
      console.error('Failed to fetch pending revisions count:', error)
    }
  }
}

function getStudentTestData(testId: number | null): StudentTesteDto | null {
  if (!testId || !roleStore.isStudent) return null
  return studentTests.value.find(st => st.testeId === testId) || null
}

function openCreateDialog() {
  createDialog.value = true
}

function navigateToRevisions() {
  router.push({
    name: 'RevisionsView',
    params: { id: route.params.id }
  })
}

async function onTestCreated() {
  createDialog.value = false
  await refreshTests()
}
</script>
<style scoped>
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}
</style>