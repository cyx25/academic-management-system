<template>
  <v-dialog v-model="dialog" max-width="600px" persistent>
    <v-card>
      <v-card-title>
        <span class="text-h5">Request Test Revision</span>
      </v-card-title>

      <v-form ref="form" @submit.prevent="submitRevision">
        <v-card-text>
          <v-textarea
            v-model="justification"
            label="Justification for Revision*"
            :rules="justificationRules"
            required
            variant="outlined"
            rows="4"
            counter="1000"
            class="mb-3"
            placeholder="Please explain why you believe your test should be reviewed..."
          ></v-textarea>

          <v-alert v-if="error" type="error" variant="tonal" class="mt-3">
            {{ error }}
          </v-alert>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn 
            color="primary" 
            type="submit"
            :loading="submitting"
            :disabled="!isFormValid"
          >
            Request Revision
          </v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import RemoteService from '@/services/RemoteService'

interface Props {
  modelValue: boolean
  studentTesteId: number // Fixed: exact prop name match
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'revision-requested': []
}>()

const dialog = ref(false)
const submitting = ref(false)
const form = ref()
const error = ref('')
const justification = ref('')

const justificationRules = [
  (v: string) => !!v || 'Justification is required',
  (v: string) => v.length <= 1000 || 'Justification must be 1000 characters or less',
  (v: string) => v.length >= 10 || 'Justification must be at least 10 characters'
]

const isFormValid = computed(() => {
  return justification.value && 
         justification.value.length >= 10 && 
         justification.value.length <= 1000
})

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    resetForm()
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

function resetForm() {
  justification.value = ''
  error.value = ''
  form.value?.resetValidation()
}

async function submitRevision() {
  const { valid } = await form.value.validate()
  if (!valid || !isFormValid.value) return

  try {
    submitting.value = true
    error.value = ''

    await RemoteService.requestTestRevision(props.studentTesteId, justification.value)
    emit('revision-requested')
    dialog.value = false
  } catch (err: any) {
    console.error('Failed to request revision:', err)
    error.value = err.response?.data?.message || err.message || 'Failed to request revision'
  } finally {
    submitting.value = false
  }
}
</script>