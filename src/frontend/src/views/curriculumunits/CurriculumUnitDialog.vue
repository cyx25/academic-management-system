<template>
  <v-dialog v-model="dialog" max-width="600">
    <template v-slot:activator="{ props }" v-if="mode === 'create'">
      <v-btn
        class="text-none font-weight-regular"
        prepend-icon="mdi-plus"
        text="Adicionar Unidade Curricular"
        v-bind="props"
        color="primary"
      ></v-btn>
    </template>

    <v-card prepend-icon="mdi-book-education" :title="mode === 'create' ? 'Nova Unidade Curricular' : 'Editar Unidade Curricular'">
      <v-form ref="form" v-model="isFormValid">
        <v-card-text>
          <v-text-field 
            label="Código*" 
            v-model="unit.code" 
            autocomplete="off" 
            required
            :rules="codeRules"
          ></v-text-field>
          <v-text-field 
            label="Nome*" 
            v-model="unit.name" 
            autocomplete="off" 
            required
            :rules="nameRules"
          ></v-text-field>
          <v-text-field
            label="Semestre*"
            v-model.number="unit.semester"
            type="number"
            min="1"
            max="2"
            autocomplete="off"
            required
            :rules="semesterRules"
          ></v-text-field>
          <v-text-field
            label="ECTS*"
            v-model.number="unit.ects"
            type="number"
            min="1"
            max="12"
            autocomplete="off"
            required
            :rules="ectsRules"
          ></v-text-field>
          <v-autocomplete
            label="Professor Regente*"
            v-model="mainTeacherId"
            :items="teachers"
            item-title="name"
            autocomplete="off"
            item-value="id"
            required
            :rules="teacherRules"
          ></v-autocomplete>
          <v-select
            label="Cursos"
            v-model="courseIds"
            :items="courses"
            item-title="name"
            autocomplete="off"
            item-value="id"
            multiple
            chips
            closable-chips
            no-data-text="Não há cursos disponíveis"
          ></v-select>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Cancelar" @click="dialog = false"></v-btn>
          <v-btn 
            color="primary" 
            text="Salvar" 
            @click="saveUnit"
            :disabled="!isFormValid"
          ></v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import RemoteService from '@/services/RemoteService'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import type PersonDto from '@/models/PersonDto'
import type CourseDto from '@/models/CourseDto'

interface Props {
  mode: 'create' | 'edit'
  modelValue?: boolean
  unit?: CurriculumUnitDto
}

const props = withDefaults(defineProps<Props>(), {
  unit: () => ({
    id: 0,
    code: '',
    name: '',
    semester: 1,
    ects: 6,
    mainTeacher: {} as PersonDto,
    courses: []
  })
})

const emit = defineEmits(['update:modelValue', 'unit-saved'])

const dialog = ref(false)
const form = ref()
const isFormValid = ref(false)
const teachers = ref<PersonDto[]>([])
const courses = ref<CourseDto[]>([])
const mainTeacherId = ref<number | null>(null)
const courseIds = ref<number[]>([])
const unit = ref<CurriculumUnitDto>({ ...props.unit })

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

const semesterRules = [
  (v: number) => !!v || 'Semestre é obrigatório',
  (v: number) => (v === 1 || v === 2) || 'Semestre deve ser 1 ou 2'
]

const ectsRules = [
  (v: number) => !!v || 'ECTS é obrigatório',
  (v: number) => (v >= 1 && v <= 12) || 'ECTS deve estar entre 1 e 12'
]

const teacherRules = [
  (v: number | null) => !!v || 'Professor Regente é obrigatório'
]

onMounted(async () => {
  teachers.value = await RemoteService.getMainTeachers()
  courses.value = await RemoteService.getCourses()
})

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    // Reset form validation when opening dialog
    form.value?.resetValidation()
    
    if (props.mode === 'create') {
      // Explicitly reset to fresh empty unit for create mode
      resetUnit()
    } else {
      // Use provided unit data for edit mode
      unit.value = { ...props.unit }
      mainTeacherId.value = props.unit.mainTeacher?.id || null
      courseIds.value = props.unit.courses?.map(c => c.id) || []
    }
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

// Helper function to reset unit data
const resetUnit = () => {
  unit.value = {
    id: 0,
    code: '',
    name: '',
    semester: 1,
    ects: 6,
    mainTeacher: {} as PersonDto,
    courses: []
  }
  mainTeacherId.value = null
  courseIds.value = []
}

const saveUnit = async () => {
  // Validate form before saving
  const { valid } = await form.value.validate()
  
  if (!valid) {
    console.log('Form validation failed')
    return
  }

  try {
    // Trim code to remove any potential whitespace
    unit.value.code = unit.value.code.trim()
    
    const mainTeacher = teachers.value.find(t => t.id === mainTeacherId.value)
    if (mainTeacher) unit.value.mainTeacher = mainTeacher
    unit.value.courses = courses.value.filter(c => courseIds.value.includes(c.id))

    if (props.mode === 'create') {
      await RemoteService.createCurriculumUnit(unit.value)
    } else {
      await RemoteService.updateCurriculumUnit(unit.value.id, unit.value)
    }
    emit('unit-saved')
    dialog.value = false
  } catch (error) {
    console.error('Error saving curriculum unit:', error)
    // You might want to show an error message to the user here
  }
}
</script>