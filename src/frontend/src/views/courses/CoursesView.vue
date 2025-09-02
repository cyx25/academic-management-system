<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Cursos</h2>
    </v-col>
    <v-col cols="auto" v-if="isAdmin">
      <div class="pa-4 text-center">
        <CourseDialog mode="create" v-model="createDialog" @course-saved="getCourses" />
      </div>

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
    <template v-slot:[`item.actions`]="{ item }" v-if="isAdmin">
      <v-tooltip text="Edit">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" @click="editCourse(item)" class="mr-2">mdi-pencil</v-icon>
        </template>
      </v-tooltip>
      <v-tooltip text="Delete">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" @click="deleteCourse(item)">mdi-delete</v-icon>
        </template>
      </v-tooltip>
    </template>
  </v-data-table>

  <CourseDialog
    mode="edit"
    v-model="editDialog"
    :course="editingCourse"
    @course-saved="getCourses"
  />

  <DeleteConfirmationDialog
    v-model="deleteDialog"
    title="Delete Course"
    message="Are you sure you want to delete this course?"
    @confirm="confirmDelete"
  />
</template>

<script setup lang="ts">
import type CourseDto from '@/models/CourseDto'
import RemoteService from '@/services/RemoteService'
import DeleteConfirmationDialog from '@/components/DeleteConfirmationDialog.vue'
import CourseDialog from './CourseDialog.vue'
import { reactive, ref, computed } from 'vue'
import { useRoleStore } from '@/stores/role'
import { fuzzySearch } from '@/utils/utilities'

let search = ref('')
let loading = ref(true)
const courses: CourseDto[] = reactive([])
const editDialog = ref(false)
const createDialog = ref(false)
const deleteDialog = ref(false)
const courseToDelete = ref<CourseDto | null>(null)
const editingCourse = ref<CourseDto>({
  id: 0,
  code: '',
  name: '',
  duration: 3
})



// Role-based access control
const roleStore = useRoleStore()
const isAdmin = computed(() => roleStore.isAdministrator)

const headers = [
  { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
  { title: 'Código', key: 'code', value: 'code', sortable: true, filterable: true },
  { title: 'Nome', key: 'name', value: 'name', sortable: true, filterable: true },
  { title: 'Duração', key: 'duration', value: 'duration', sortable: true, filterable: true },
 
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false, filterable: false }
]



getCourses()
async function getCourses() {
  courses.splice(0, courses.length)
  courses.push(...(await RemoteService.getCourses()))
  loading.value = false
  console.log(courses)
}

const editCourse = (course: CourseDto) => {
  editingCourse.value = { ...course }
  editDialog.value = true
}

const deleteCourse = (course: CourseDto) => {
  courseToDelete.value = course
  deleteDialog.value = true
}

const confirmDelete = async () => {
  if (courseToDelete.value) {
    await RemoteService.deleteCourse(courseToDelete.value.id)
    getCourses()
    courseToDelete.value = null
  }
}
</script>