<template>
  <v-dialog v-model="dialog" max-width="500">
    <template v-slot:activator="{ props }" v-if="mode === 'create'">
      <v-btn
        class="text-none font-weight-regular"
        prepend-icon="mdi-plus"
        text="Adicionar Curso"
        v-bind="props"
        color="primary"
      ></v-btn>
    </template>

    <v-card prepend-icon="mdi-school" :title="mode === 'create' ? 'Novo Curso' : 'Editar Curso'">
      <v-form ref="form" v-model="isFormValid">
        <v-card-text>
          <v-text-field 
            label="Código*" 
            v-model="course.code" 
            autocomplete="off" 
            required
            :rules="codeRules"
          ></v-text-field>
          <v-text-field 
            label="Nome*" 
            v-model="course.name" 
            autocomplete="off" 
            required
            :rules="nameRules"
          ></v-text-field>
          <v-text-field
            label="Duração (anos)*"
            v-model.number="course.duration"
            type="number"
            min="1"
            max="5"
            autocomplete="off"
            required
            :rules="durationRules"
          ></v-text-field>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Cancelar" @click="dialog = false"></v-btn>
          <v-btn 
            color="primary" 
            text="Salvar" 
            @click="saveCourse"
            :disabled="!isFormValid"
          ></v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import RemoteService from '@/services/RemoteService'
import type CourseDto from '@/models/CourseDto'

interface Props {
  mode: 'create' | 'edit'
  modelValue: boolean
  course?: CourseDto
}

const props = withDefaults(defineProps<Props>(), {
  course: () => ({ id: 0, code: '', name: '', duration: 3 })
})

const emit = defineEmits(['update:modelValue', 'course-saved'])

const dialog = ref(false)
const form = ref()
const isFormValid = ref(false)
const course = ref<CourseDto>({ ...props.course })

// Validation rules
const codeRules = [
  (v: string) => !!v || 'Código é obrigatório',
  (v: string) => !/\s/.test(v) || 'Código não pode conter espaços',
  (v: string) => v.length > 0 || 'Código não pode estar vazio'
]

const nameRules = [
  (v: string) => !!v || 'Nome é obrigatório',
  (v: string) => v.trim().length > 0 || 'Nome não pode estar vazio'
]

const durationRules = [
  (v: number) => !!v || 'Duração é obrigatória',
  (v: number) => (v >= 1 && v <= 5) || 'Duração deve estar entre 1 e 5 anos'
]

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    course.value = { ...props.course }
    // Reset form validation when opening dialog
    form.value?.resetValidation()
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

const saveCourse = async () => {
  // Validate form before saving
  const { valid } = await form.value.validate()
  
  if (!valid) {
    console.log('Form validation failed')
    return
  }

  try {
    // Trim code to remove any potential whitespace
    course.value.code = course.value.code.trim()
    
    if (props.mode === 'create') {
      await RemoteService.createCourse(course.value)
    } else {
      await RemoteService.updateCourse(course.value.id, course.value)
    }
    emit('course-saved')
    dialog.value = false
  } catch (error) {
    console.error('Error saving course:', error)
    // You might want to show an error message to the user here
  }
}
</script>