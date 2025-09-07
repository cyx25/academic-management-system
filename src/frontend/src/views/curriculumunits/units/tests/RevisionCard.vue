<template>
  <v-card outlined elevation="2" class="mb-4 themed-card">
    <v-card-title class="d-flex justify-space-between align-items-center">
      <div>
        <span class="text-h6 font-weight-bold">{{ revision.studentName }}</span>
        <v-chip size="small" color="warning" variant="tonal" class="ml-2">
          {{ revision.status }}
        </v-chip>
      </div>
      <div class="text-caption text-medium-emphasis">
        {{ formatDate(revision.requestDate) }}
      </div>
    </v-card-title>

    <v-card-subtitle>
      <strong>Teste:</strong> {{ revision.testeTitle }} | 
      <strong>Nota atual:</strong> {{ revision.currentGrade }}/20
    </v-card-subtitle>

    <v-card-text>
      <div class="mb-3">
        <strong>Justificação do Aluno:</strong>
        <p class="mt-1">{{ revision.justification }}</p>
      </div>

      <!-- Grading Form for TA -->
      <v-form v-if="roleStore.isTeachingAssistant && revision.status === 'PENDING'" 
              ref="gradingForm" 
              @submit.prevent="submitNewGrade">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model.number="newGrade"
              label="Nova nota (0-20)*"
              type="number"
              min="0"
              max="20"
              step="0.1"
              :rules="gradeRules"
              variant="outlined"
              required
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-center">
            <v-btn
              color="primary"
              type="submit"
              :loading="submitting"
              :disabled="!isGradeValid"
            >
              Submeter Nova Nota
            </v-btn>
          </v-col>
        </v-row>
      </v-form>

      <!-- TA Grade Pending Approval -->
      <v-alert v-if="revision.status === 'TA_GRADED'" 
               type="info" 
               variant="tonal" 
               class="mb-3">
        <div class="d-flex justify-space-between align-items-center">
          <div>
            <strong>Nota do Professor Assistente:</strong> {{ revision.newGrade }}/20
            <br>
            <strong>Atribuido por:</strong> {{ revision.reviewedBy }}
          </div>
          <!-- Approval buttons for Main Teacher -->
          <div v-if="roleStore.isMainTeacher" class="d-flex gap-2">
            <v-btn 
              color="success" 
              size="small"
              @click="approveGrade"
              :loading="approvingGrade"
            >
              Approvar
            </v-btn>
            <v-btn 
              color="error" 
              size="small"
              @click="rejectGrade"
              :loading="rejectingGrade"
            >
              Rejeitar
            </v-btn>
          </div>
        </div>
      </v-alert>

      <!-- Final Status -->
      <v-alert v-if="['APPROVED', 'REJECTED'].includes(revision.status)" 
               :type="revision.status === 'APPROVED' ? 'success' : 'error'" 
               variant="tonal">
        Revisão {{ revision.status.toLowerCase() }} by {{ revision.reviewedBy }}
        <div v-if="revision.status === 'APPROVED'" class="mt-1">
          <strong>Nota Final:</strong> {{ revision.newGrade }}/20
        </div>
      </v-alert>

      <v-alert v-if="error" type="error" variant="tonal" class="mt-3">
        {{ error }}
      </v-alert>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type RevisionDto from '@/models/RevisionDto'

interface Props {
  revision: RevisionDto
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'revision-graded': []
}>()

const roleStore = useRoleStore()
const gradingForm = ref()
const newGrade = ref<number | null>(null)
const submitting = ref(false)
const approvingGrade = ref(false)
const rejectingGrade = ref(false)
const error = ref('')

const gradeRules = [
  (v: number) => v !== null && v !== undefined || 'Grade is required',
  (v: number) => v >= 0 || 'Grade must be at least 0',
  (v: number) => v <= 20 || 'Grade must be at most 20'
]

const isGradeValid = computed(() => {
  return newGrade.value !== null && 
         newGrade.value !== undefined && 
         newGrade.value >= 0 && 
         newGrade.value <= 20
})

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function submitNewGrade() {
  const { valid } = await gradingForm.value.validate()
  if (!valid || !isGradeValid.value) return

  try {
    submitting.value = true
    error.value = ''

    await RemoteService.gradeRevision(
      props.revision.id, 
      roleStore.getCurrentUserID!, 
      newGrade.value!
    )
    emit('revision-graded')
  } catch (err: any) {
    console.error('Failed to submit new grade:', err)
    error.value = err.response?.data?.message || err.message || 'Failed to submit grade'
  } finally {
    submitting.value = false
  }
}

async function approveGrade() {
  try {
    approvingGrade.value = true
    error.value = ''

    await RemoteService.processRevision(
      props.revision.id, 
      'APPROVE', 
      roleStore.getCurrentUserID!
    )
    emit('revision-graded')
  } catch (err: any) {
    console.error('Failed to approve grade:', err)
    error.value = err.response?.data?.message || err.message || 'Failed to approve'
  } finally {
    approvingGrade.value = false
  }
}

async function rejectGrade() {
  try {
    rejectingGrade.value = true
    error.value = ''

    await RemoteService.processRevision(
      props.revision.id, 
      'REJECT', 
      roleStore.getCurrentUserID!
    )
    emit('revision-graded')
  } catch (err: any) {
    console.error('Failed to reject grade:', err)
    error.value = err.response?.data?.message || err.message || 'Failed to reject'
  } finally {
    rejectingGrade.value = false
  }
}
</script>
<style scoped>
.themed-card {
  background: rgba(var(--v-theme-surface), 0.9);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.themed-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>