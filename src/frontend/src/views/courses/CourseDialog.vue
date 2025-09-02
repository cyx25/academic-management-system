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
      <v-card-text>
        <v-text-field label="Código*" v-model="course.code" required></v-text-field>
        <v-text-field label="Nome*" v-model="course.name" required></v-text-field>
        <v-text-field
          label="Duração (anos)*"
          v-model.number="course.duration"
          type="number"
          min="1"
          max="5"
          required
        ></v-text-field>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Cancelar" @click="dialog = false"></v-btn>
        <v-btn color="primary" text="Salvar" @click="saveCourse"></v-btn>
      </v-card-actions>
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
const course = ref<CourseDto>({ ...props.course })

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    course.value = { ...props.course }
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

const saveCourse = async () => {
  if (props.mode === 'create') {
    await RemoteService.createCourse(course.value)
  } else {
    await RemoteService.updateCourse(course.value.id, course.value)
  }
  emit('course-saved')
  dialog.value = false
}
</script>