<template>
  <v-dialog v-model="dialog" max-width="500" persistent>
    <v-card>
      <v-card-title class="pa-6 pb-4">
        <div class="d-flex align-center">
          <v-icon icon="mdi-plus-circle" color="primary" size="24" class="mr-3"></v-icon>
          <h2 class="text-h6 font-weight-medium">Create Material Tab</h2>
        </div>
      </v-card-title>

      <v-card-text class="pa-6 pt-0">
        <v-form ref="form" v-model="valid">
          <v-text-field
            v-model="material.name"
            label="Material Name"
            placeholder="e.g., Lecture Slides, Lab Materials"
            variant="outlined"
            density="comfortable"
            :rules="nameRules"
            class="mb-4"
          ></v-text-field>

          <div class="mb-4">
            <label class="text-body-2 font-weight-medium mb-2 d-block">Choose Icon</label>
            <div class="icon-grid">
              <v-btn
                v-for="icon in availableIcons"
                :key="icon.name"
                :variant="material.iconName === icon.name ? 'elevated' : 'tonal'"
                :color="material.iconName === icon.name ? 'primary' : 'surface-variant'"
                size="large"
                class="icon-btn"
                @click="material.iconName = icon.name"
              >
                <v-icon :icon="icon.name" size="24"></v-icon>
              </v-btn>
            </div>
          </div>

          <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
            {{ error }}
          </v-alert>
        </v-form>
      </v-card-text>

      <v-card-actions class="pa-6 pt-0">
        <v-spacer></v-spacer>
        <v-btn variant="text" @click="closeDialog">Cancel</v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          :disabled="!valid || loading"
          :loading="loading"
          @click="createMaterial"
        >
          Create Material
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import RemoteService from '@/services/RemoteService'
import type { CreateMaterialDto } from '@/models/MaterialDto'

interface Props {
  modelValue: boolean
  unitId: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'material-created': []
}>()

const dialog = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
})

const form = ref()
const valid = ref(false)
const loading = ref(false)
const error = ref('')

const material = ref<Omit<CreateMaterialDto, 'curriculumUnitId'>>({
  name: '',
  iconName: 'mdi-book-open-variant'
})

const nameRules = [
  (v: string) => !!v || 'Material name is required',
  (v: string) => v.length <= 100 || 'Name must be 100 characters or less'
]

const availableIcons = [
  { name: 'mdi-book-open-variant' },
  { name: 'mdi-file-document' },
  { name: 'mdi-presentation' },
  { name: 'mdi-video' },
  { name: 'mdi-image' },
  { name: 'mdi-download' },
  { name: 'mdi-clipboard-text' },
  { name: 'mdi-calculator' },
  { name: 'mdi-chart-line' },
  { name: 'mdi-code-tags' },
  { name: 'mdi-database' },
  { name: 'mdi-web' }
]

watch(dialog, (newValue) => {
  if (newValue) {
    resetForm()
  }
})

function resetForm() {
  material.value = {
    name: '',
    iconName: 'mdi-book-open-variant'
  }
  error.value = ''
  form.value?.resetValidation()
}

async function createMaterial() {
  if (!form.value.validate()) return

  try {
    loading.value = true
    error.value = ''

    const createDto: CreateMaterialDto = {
      ...material.value,
      curriculumUnitId: props.unitId
    }

    await RemoteService.createMaterial(createDto)
    emit('material-created')
  } catch (err: any) {
    error.value = err.message || 'Failed to create material'
  } finally {
    loading.value = false
  }
}

function closeDialog() {
  dialog.value = false
}
</script>

<style scoped>
.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.icon-btn {
  aspect-ratio: 1;
  min-width: 48px;
}
</style>