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
      <v-card-text>
        <v-text-field label="Código*" v-model="unit.code" required></v-text-field>
        <v-text-field label="Nome*" v-model="unit.name" required></v-text-field>
        <v-text-field
          label="Semestre*"
          v-model.number="unit.semester"
          type="number"
          min="1"
          max="2"
          required
        ></v-text-field>
        <v-text-field
          label="ECTS*"
          v-model.number="unit.ects"
          type="number"
          min="1"
          max="12"
          required
        ></v-text-field>
        <v-autocomplete
          label="Professor Regente*"
          v-model="mainTeacherId"
          :items="teachers"
          item-title="name"
          item-value="id"
          required
        ></v-autocomplete>
        <v-select
          label="Cursos"
          v-model="courseIds"
          :items="courses"
          item-title="name"
          item-value="id"
          multiple
          chips
          closable-chips
        ></v-select>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Cancelar" @click="dialog = false"></v-btn>
        <v-btn color="primary" text="Salvar" @click="saveUnit"></v-btn>
      </v-card-actions>
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
const teachers = ref<PersonDto[]>([])
const courses = ref<CourseDto[]>([])
const mainTeacherId = ref<number | null>(null)
const courseIds = ref<number[]>([])
const unit = ref<CurriculumUnitDto>({ ...props.unit })

onMounted(async () => {
  teachers.value = await RemoteService.getMainTeachers()
  courses.value = await RemoteService.getCourses()
})

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    unit.value = { ...props.unit }
    mainTeacherId.value = props.unit.mainTeacher?.id || null

    // Preserve courses only in edit mode
    if (props.mode === 'edit') {
      courseIds.value = props.unit.courses?.map(c => c.id) || []
    } else {
      courseIds.value = [] // Reset for create mode
    }
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

const saveUnit = async () => {
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
}
</script>