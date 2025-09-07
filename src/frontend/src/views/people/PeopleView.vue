<template>
  <v-row align="center" :class="{ 'pa-4 pl-0': !isAdmin }">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Pessoas</h2>
    </v-col>
    <v-col cols="auto" v-if="isAdmin">
      <div class="pa-4 text-center">
        <PersonDialog mode="create" v-model="createDialog" @person-saved="getPeople" />
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
    :items="people"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem pessoas a apresentar."
  >
    <template v-slot:[`item.type`]="{ item }">
      <v-chip v-if="item.type === 'ADMINISTRATOR'" color="purple" text-color="white">
        Administrador
      </v-chip>
      <v-chip v-else-if="item.type === 'MAIN_TEACHER'" color="red" text-color="white">
        Professor Regente
      </v-chip>
      <v-chip v-else-if="item.type === 'TEACHING_ASSISTANT'" color="blue" text-color="white">
        Professor Assistente
      </v-chip>
      <v-chip v-else color="green" text-color="white">
        Aluno
      </v-chip>
    </template>
    
    <template v-slot:[`item.actions`]="{ item }">
      <!-- Student Dashboard Icon - Only for students and only visible to admins -->
      <v-tooltip v-if="item.type === 'STUDENT' && isAdmin" text="Ver Dashboard do Estudante">
        <template v-slot:activator="{ props }">
          <v-icon 
            v-bind="props" 
            @click="openStudentDashboard(item)" 
            class="mr-2"
            color="primary"
          >
            mdi-account-school
          </v-icon>
        </template>
      </v-tooltip>

      <!-- Regular Admin Actions -->
      <template v-if="isAdmin">
        <v-tooltip text="Edit">
          <template v-slot:activator="{ props }">
            <v-icon v-bind="props" @click="editPerson(item)" class="mr-2">mdi-pencil</v-icon>
          </template>
        </v-tooltip>
        <v-tooltip text="Delete">
          <template v-slot:activator="{ props }">
            <v-icon v-bind="props" @click="deletePerson(item)">mdi-delete</v-icon>
          </template>
        </v-tooltip>
      </template>
    </template>
  </v-data-table>

  <!-- Existing Dialogs -->
  <PersonDialog
    mode="edit"
    v-model="editDialog"
    :person="editingPerson"
    @person-saved="getPeople"
  />
  <DeleteConfirmationDialog
    v-model="deleteDialog"
    title="Delete Person"
    message="Are you sure you want to delete this person?"
    @confirm="confirmDelete"
  />

  <!-- New Student Dashboard Dialog -->
  <StudentDashboardDialog
    v-model="studentDashboardDialog"
    :student="selectedStudent"
  />
</template>

<script setup lang="ts">
import type PersonDto from '@/models/PersonDto'
import RemoteService from '@/services/RemoteService'
import PersonDialog from './PersonDialog.vue'
import { reactive, ref, computed } from 'vue'
import { fuzzySearch } from '@/utils/utilities'
import DeleteConfirmationDialog from '@/components/DeleteConfirmationDialog.vue'
import StudentDashboardDialog from '@/components/StudentDashboardDialog.vue'
import { useRoleStore } from '../../stores/role'

let search = ref('')
let loading = ref(true)
const people: PersonDto[] = reactive([])
const editDialog = ref(false)
const createDialog = ref(false)
const deleteDialog = ref(false)
const studentDashboardDialog = ref(false)
const personToDelete = ref<PersonDto | null>(null)
const selectedStudent = ref<PersonDto | null>(null)
const editingPerson = ref<PersonDto>({
  id: 0,
  name: '',
  istId: '',
  email: '',
  type: ''
})

// Role-based access control
const roleStore = useRoleStore()
const isAdmin = computed(() => roleStore.isAdministrator)

// Update headers to be conditional based on admin status
const headers = computed(() => {
  const baseHeaders = [
    { title: 'ID', key: 'id', value: 'id', sortable: true, filterable: false },
    {
      title: 'Nome',
      key: 'name',
      value: 'name',
      sortable: true,
      filterable: true
    },
    {
      title: 'IST ID',
      key: 'istId',
      value: 'istId',
      sortable: true,
      filterable: true
    },
    {
      title: "Email",
      key: 'email',
      value: 'email',
      sortable: true,
      filterable: true
    },
    {
      title: 'Tipo',
      key: 'type',
      value: 'type',
      sortable: true,
      filterable: true
    }
  ]

  // Only add actions column for admins
  if (isAdmin.value) {
    baseHeaders.push({
      title: 'Ações',
      key: 'actions',
      value: 'actions',
      sortable: false,
      filterable: false
    })
  }

  return baseHeaders
})

// Existing methods
getPeople()
async function getPeople() {
  people.splice(0, people.length)
  people.push(...(await RemoteService.getPeople()))
  loading.value = false
  console.log(people)
}

const editPerson = (person: PersonDto) => {
  editingPerson.value = { ...person }
  editDialog.value = true
}

const deletePerson = (person: PersonDto) => {
  personToDelete.value = person
  deleteDialog.value = true
}

const confirmDelete = async () => {
  if (personToDelete.value) {
    await RemoteService.deletePerson(personToDelete.value.id)
    getPeople()
    personToDelete.value = null
  }
}

// New method for opening student dashboard
const openStudentDashboard = (student: PersonDto) => {
  selectedStudent.value = student
  studentDashboardDialog.value = true
}
</script>
