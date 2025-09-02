<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem Unidades Curriculares</h2>
    </v-col>

    <v-col cols="auto" v-if="isAdmin">
      <div class="pa-4 text-center">
        <CurriculumUnitDialog mode="create" @unit-saved="getCurriculumUnits" />
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
    :items="curriculumUnits"
    :search="search"
    :loading="loading"
    :custom-filter="fuzzySearch"
    item-key="id"
    class="text-left"
    no-data-text="Sem unidades curriculares a apresentar."
  >
    <template v-slot:[`item.actions`]="{ item }" v-if="isAdmin">
      <v-tooltip text="Edit">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" @click="editCurriculumUnit(item)" class="mr-2">mdi-pencil</v-icon>
        </template>
      </v-tooltip>
      <v-tooltip text="Delete">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" @click="deleteCurriculumUnit(item)">mdi-delete</v-icon>
        </template>
      </v-tooltip>
    </template>
  </v-data-table>

  <CurriculumUnitDialog
    mode="edit"
    v-model="editDialog"
    :unit="editingUnit"
    @unit-saved="getCurriculumUnits"
    v-if="isAdmin"
  />

  <DeleteConfirmationDialog
    v-model="deleteDialog"
    title="Delete Curriculum Unit"
    message="Are you sure you want to delete this curriculum unit?"
    @confirm="confirmDelete"
  />

</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import RemoteService from '@/services/RemoteService'
import DeleteConfirmationDialog from '@/components/DeleteConfirmationDialog.vue'
import CurriculumUnitDialog from './CurriculumUnitDialog.vue'
import { useRoleStore } from '@/stores/role'
import { fuzzySearch } from '@/utils/utilities'
import PersonDto from '../../models/PersonDto'

let search = ref('')
let loading = ref(true)
const curriculumUnits: CurriculumUnitDto[] = reactive([])
const editDialog = ref(false)
const deleteDialog = ref(false)
const unitToDelete = ref<CurriculumUnitDto | null>(null)
const editingUnit = ref<CurriculumUnitDto>({
  id: 0,
  code: '',
  name: '',
  semester: 1,
  ects: 6,
  mainTeacher: {} as PersonDto,
  courses: []
})

// Role-based access control
const roleStore = useRoleStore()
const isAdmin = computed(() => roleStore.isAdministrator)


// Table headers
const headers = [
  { title: 'Código', key: 'code', value: 'code', sortable: true },
  { title: 'Nome', key: 'name', value: 'name', sortable: true },
  { title: 'Semestre', key: 'semester', value: 'semester', sortable: true },
  { title: 'ECTS', key: 'ects', value: 'ects', sortable: true },
  { title: 'Professor Regente', key: 'mainTeacher', value: 'mainTeacher.name', sortable: true },
  { title: 'Ações', key: 'actions', value: 'actions', sortable: false }
]

// Fetch curriculum units
getCurriculumUnits()
async function getCurriculumUnits() {
  loading.value = true
  try {
    curriculumUnits.splice(0, curriculumUnits.length)
    curriculumUnits.push(...(await RemoteService.getCurriculumUnits()))
  } finally {
    loading.value = false
  }
}

// Edit curriculum unit
const editCurriculumUnit = (unit: CurriculumUnitDto) => {
  console.log('Editing curriculum unit:', unit)
  editingUnit.value = { ...unit }
  editDialog.value = true
}

// Delete curriculum unit
const deleteCurriculumUnit = (unit: CurriculumUnitDto) => {
  unitToDelete.value = unit
  deleteDialog.value = true
}

const confirmDelete = async () => {
  if (unitToDelete.value) {
    await RemoteService.deleteCurriculumUnit(unitToDelete.value.id)
    getCurriculumUnits()
    unitToDelete.value = null
  }
}
</script>