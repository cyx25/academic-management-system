<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Cursos</h2>
    </v-col>
    <v-col cols="auto">
      <CreateCourseDialog @course-created="getCourses" />
    </v-col>
  </v-row>

  <v-text-field
    v-model="search"
    label="Search"
    prepend-inner-icon="mdi-magnify"
    variant="outlined"
    hide-details
    single-line
  ></v-text-field>

  <v-data-table
    :headers="headers"
    :items="courses"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem cursos a apresentar."
  >
    <template v-slot:[`item.duration`]="{ item }">
      {{ item.duration }} anos
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon @click="editCourse(item)" class="mr-2">mdi-pencil</v-icon>
      <v-icon @click="deleteCourse(item)">mdi-delete</v-icon>
    </template>
  </v-data-table>
</template>

<script setup lang="ts">
import type CourseDto from '@/models/CourseDto'
import RemoteService from '@/services/RemoteService'
import CreateCourseDialog from './CreateCourseDialog.vue'
import { reactive, ref } from 'vue'
import { fuzzySearch } from '@/utils/utilities'

let search = ref('')
let loading = ref(true)
const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
  {
    title: 'Código',
    key: 'code',
    value: 'code',
    sortable: true,
    filterable: true
  },
  {
    title: 'Nome',
    key: 'name',
    value: 'name',
    sortable: true,
    filterable: true
  },
  {
    title: 'Duração',
    key: 'duration',
    value: 'duration',
    sortable: true,
    filterable: true
  },
  {
    title: 'Ações',
    key: 'actions',
    value: 'actions',
    sortable: false,
    filterable: false
  }
]

const courses: CourseDto[] = reactive([])

getCourses()
async function getCourses() {
  courses.splice(0, courses.length)
  courses.push(...(await RemoteService.getCourses()))
  loading.value = false
  console.log(courses)
}

const editCourse = (course: CourseDto) => {
  console.log('Editing course:', course)
}

const deleteCourse = (course: CourseDto) => {
  console.log('Deleting course:', course)
}


</script>