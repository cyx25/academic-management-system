<template>
  <v-dialog v-model="dialog" max-width="600px" persistent>
    <v-card>
      <v-card-title>
        <span class="text-h5">Criar novo Teste</span>
      </v-card-title>

      <v-form ref="form" @submit.prevent="saveTest">
        <v-card-text>
          <v-text-field
            v-model="testData.title"
            label="Título*"
            :rules="titleRules"
            required
            variant="outlined"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model.number="testData.weight"
            label="Peso (%)*"
            type="number"
            min="0"
            max="100"
            :rules="weightRules"
            required
            variant="outlined"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="testData.date"
            label="Data e Hora*"
            type="datetime-local"
            :rules="dateRules"
            required
            variant="outlined"
            class="mb-3"
          ></v-text-field>

          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model.number="durationHours"
                label="Duração (Horas)"
                type="number"
                min="0"
                max="23"
                variant="outlined"
                :rules="[v => v >= 0 || 'Horas devem ser 0 ou mais']"
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                v-model.number="durationMinutes"
                label="Duração (Minutos)"
                type="number"
                min="0"
                max="59"
                variant="outlined"
                :rules="[v => v >= 0 && v <= 59 || 'Minutos devem estar entre 0 e 59']"
              ></v-text-field>
            </v-col>
          </v-row>

          <!-- Duration Preview -->
          <v-alert v-if="durationDisplay" type="info" variant="tonal" class="mb-3">
            <div class="d-flex align-center">
              <v-icon icon="mdi-clock" class="mr-2"></v-icon>
              <span>Duração: {{ durationDisplay }}</span>
            </div>
          </v-alert>

          <v-alert v-if="error" type="error" variant="tonal" class="mt-3">
            {{ error }}
          </v-alert>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="dialog = false">Cancelar</v-btn>
          <v-btn 
            color="primary" 
            type="submit"
            :loading="saving"
            :disabled="!isFormValid"
          >
            Criar Teste
          </v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import RemoteService from '@/services/RemoteService'
import type { CreateTesteDto } from '@/models/TesteDto'

interface Props {
  modelValue: boolean
  unitId: number
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'test-created'])

const dialog = ref(false)
const saving = ref(false)
const form = ref()
const error = ref('')

const testData = ref<CreateTesteDto>({
  title: '',
  weight: 0,
  date: '',
  duration: ''
})

const durationHours = ref(0)
const durationMinutes = ref(0)

// Validation rules
const titleRules = [
  (v: string) => !!v || 'Title is required',
  (v: string) => v.length <= 100 || 'Title must be 100 characters or less'
]

const weightRules = [
  (v: number) => v !== null && v !== undefined || 'Weight is required',
  (v: number) => v >= 0 || 'Weight must be at least 0',
  (v: number) => v <= 100 || 'Weight must not exceed 100'
]

const dateRules = [
  (v: string) => !!v || 'Date is required',
  (v: string) => new Date(v) > new Date() || 'Date must be in the future'
]

// Computed properties
const durationDisplay = computed(() => {
  if (durationHours.value === 0 && durationMinutes.value === 0) {
    return ''
  }
  
  const parts = []
  if (durationHours.value > 0) {
    parts.push(`${durationHours.value}h`)
  }
  if (durationMinutes.value > 0) {
    parts.push(`${durationMinutes.value}m`)
  }
  
  return parts.join(' ')
})

const isFormValid = computed(() => {
  return testData.value.title && 
         testData.value.weight >= 0 && 
         testData.value.weight <= 100 && 
         testData.value.date &&
         (durationHours.value > 0 || durationMinutes.value > 0)
})

// Watch for dialog changes
watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    resetForm()
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

// Watch duration changes and build duration string
watch([durationHours, durationMinutes], () => {
  testData.value.duration = durationDisplay.value
})

function resetForm() {
  testData.value = {
    title: '',
    weight: 0,
    date: '',
    duration: ''
  }
  durationHours.value = 0
  durationMinutes.value = 0
  error.value = ''
  form.value?.resetValidation()
}

async function saveTest() {
  const { valid } = await form.value.validate()
  if (!valid || !isFormValid.value) return

  try {
    saving.value = true
    error.value = ''

    // Ensure duration is set
    testData.value.duration = durationDisplay.value

    await RemoteService.createTest(props.unitId, testData.value)
    emit('test-created')
    dialog.value = false
  } catch (err: any) {
    console.error('Failed to create test:', err)
    error.value = err.response?.data?.message || err.message || 'Failed to create test'
  } finally {
    saving.value = false
  }
}
</script>