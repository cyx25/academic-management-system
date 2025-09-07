<template>
  <v-container fluid class="pa-4">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <h2 class="text-h4 mb-6 text-center font-weight-bold text-darken-2" :style="{ color: primaryColor }">Pessoal da Unidade Curricular</h2>

        <!-- Professor Regente -->
        <v-card class="mb-6" :color="cardColor" elevation="4" rounded="lg">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2" :color="iconColor">mdi-account-tie</v-icon>
            Professor Regente
          </v-card-title>
          <v-card-text v-if="unit?.mainTeacher" class="pa-4">
            <v-row>
              <v-col cols="12" sm="6" md="3">
                <v-text-field
                  :model-value="unit.mainTeacher.name"
                  label="Nome"
                  readonly
                  variant="outlined"
                  prepend-inner-icon="mdi-account"
                  :color="primaryColor"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <v-text-field
                  :model-value="unit.mainTeacher.istId"
                  label="IST ID"
                  readonly
                  variant="outlined"
                  prepend-inner-icon="mdi-identifier"
                  :color="primaryColor"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <v-text-field
                  :model-value="unit.mainTeacher.email"
                  label="Email"
                  readonly
                  variant="outlined"
                  prepend-inner-icon="mdi-email"
                  :color="primaryColor"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-text v-else class="text-grey text-center pa-4">
            <v-icon class="mr-2">mdi-account-off</v-icon>
            Nenhum professor regente associado.
          </v-card-text>
        </v-card>

        <!-- Teaching Assistants -->
        <v-card class="mb-6" :color="cardColor" elevation="4" rounded="lg">
          <v-card-title class="d-flex justify-space-between align-center">
            <div class="d-flex align-center">
              <v-icon class="mr-2" :color="iconColor">mdi-account-group</v-icon>
              Professores Assistentes ({{ assistants.length }})
            </div>
            <v-btn v-if="canManage" :color="buttonColor" @click="openAddDialog('assistant')" prepend-icon="mdi-plus">
              Adicionar Assistente
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-data-table
              :headers="personHeaders"
              :items="assistants"
              item-key="id"
              :items-per-page="5"
              class="text-left"
              hide-default-footer
              no-data-text="Nenhum assistente associado."
            >
              <template v-slot:[`item.type`]="{ item }">
                <v-chip v-if="item.type === 'TEACHING_ASSISTANT'" color="purple" text-color="white">
                  Professor Assistente
                </v-chip>
              </template>
              <template v-slot:[`item.actions`]="{ item }">
                <v-tooltip text="Remover">
                  <template v-slot:activator="{ props }">
                    <v-icon
                      v-if="canManage"
                      v-bind="props"
                      color="error"
                      @click="removePerson(item, 'assistant')"
                    >
                      mdi-delete
                    </v-icon>
                  </template>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>

        <!-- Students -->
        <v-card :color="cardColor" elevation="4" rounded="lg">
          <v-card-title class="d-flex justify-space-between align-center">
            <div class="d-flex align-center">
              <v-icon class="mr-2" :color="iconColor">mdi-school</v-icon>
              Alunos ({{ students.length }})
            </div>
            <v-btn v-if="canManage" :color="buttonColor" @click="openAddDialog('student')" prepend-icon="mdi-plus">
              Adicionar Aluno
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-data-table
              :headers="studentHeaders"
              :items="students"
              item-key="id"
              :items-per-page="5"
              class="text-left"
              hide-default-footer
              autocomplete="off"
              no-data-text="Nenhum aluno associado."
            >
              <template v-slot:[`item.status`]="{ item }">
                <v-chip v-if="item.status === 'ENROLLED'" color="blue" text-color="white">
                  INSCRITO
                </v-chip>
                <v-chip v-if="item.status === 'APPROVED'" color="green" text-color="white">
                  APROVADO
                </v-chip>
                <v-chip v-if="item.status === 'FAILED'" color="red" text-color="white">
                  REPROVADO
                </v-chip>
              </template>

              <!-- New Final Grade Column -->
              <template v-slot:[`item.finalGrade`]="{ item }">
                <div v-if="item.finalGrade !== null">
                  <v-chip 
                    :color="getGradeColor(item.finalGrade)" 
                    variant="elevated"
                    class="font-weight-bold"
                    size="small"
                  >
                    {{ item.finalGrade }}/20
                  </v-chip>
                </div>
                <span v-else class="text-medium-emphasis">-</span>
              </template>

                <template v-slot:[`item.actions`]="{ item }">
                <div class="d-flex justify-end align-center gap-1" style="min-width: 100px;">
                  <!-- Grade Breakdown Button -->
                  <v-tooltip v-if="item.finalGrade !== null" text="Ver detalhes da nota">
                  <template v-slot:activator="{ props }">
                    <v-btn
                    v-bind="props"
                    icon="mdi-calculator"
                    variant="text"
                    color="primary"
                    size="small"
                    @click="openGradeBreakdown(item)"
                    ></v-btn>
                  </template>
                  </v-tooltip>
                  
                  <!-- Remove Button -->
                  <v-tooltip text="Remover">
                  <template v-slot:activator="{ props }">
                    <v-icon
                    v-if="canManage"
                    v-bind="props"
                    color="error"
                    @click="removePerson(item, 'student')"
                    >
                    mdi-delete
                    </v-icon>
                  </template>
                  </v-tooltip>
                </div>
                </template>
            </v-data-table>
          </v-card-text>
        </v-card>

        <!-- Add Person Dialog -->
        <v-dialog v-model="addDialog" max-width="600px" persistent>
          <v-card :color="cardColor" elevation="8" rounded="lg">
            <v-card-title class="d-flex align-center">
              <v-icon class="mr-2" :color="iconColor">mdi-plus</v-icon>
              Adicionar {{ personTypeToAdd === 'student' ? 'Aluno' : 'Assistente' }}
            </v-card-title>
            <v-card-text class="pa-6">
              <v-autocomplete
                v-model="selectedPerson"
                :items="filteredPeople"
                item-title="name"
                label="Procurar pessoa..."
                return-object
                clearable
                :color="primaryColor"
                variant="outlined"
                prepend-inner-icon="mdi-magnify"
                autocomplete="off"
                :no-data-text="`Não há ${personTypeToAdd === 'student' ? 'alunos' : 'assistentes'} disponíveis`"
              >
                <template v-slot:item="{ props, item }">
                  <v-list-item v-bind="props">
                    <v-list-item-subtitle>{{ 'istID: ' + item.raw.istId }}</v-list-item-subtitle>
                  </v-list-item>
                </template>
              </v-autocomplete>
            </v-card-text>
            <v-card-actions class="pa-6">
              <v-spacer></v-spacer>
              <v-btn variant="text" @click="addDialog = false">Cancelar</v-btn>
              <v-btn class="font-weight-bold text-darken-2" @click="confirmAddPerson" prepend-icon="mdi-check">
                Adicionar
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Grade Breakdown Dialog -->
        <GradeBreakdownDialog
          v-model="gradeBreakdownDialog"
          :student-id="selectedStudentForGrade?.id || null"
          :curriculum-unit-id="unitId"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import type PersonDto from '@/models/PersonDto'
import type EnrollmentDto from '@/models/EnrollmentDto'
import GradeBreakdownDialog from '@/components/GradeBreakdownDialog.vue'

// Interface to extend PersonDto with enrollment properties
interface StudentWithGrade extends PersonDto {
  status: 'ENROLLED' | 'APPROVED' | 'FAILED'
  finalGrade: number | null
}

const route = useRoute()
const roleStore = useRoleStore()

const unitId = Number(route.params.id)
const unit = ref<CurriculumUnitDto | null>(null)
const assistants = ref<PersonDto[]>([])
const students = ref<StudentWithGrade[]>([])
const allPeople = ref<PersonDto[]>([])

const addDialog = ref(false)
const gradeBreakdownDialog = ref(false)
const personTypeToAdd = ref<'student' | 'assistant'>('student')
const selectedPerson = ref<PersonDto | null>(null)
const selectedStudentForGrade = ref<StudentWithGrade | null>(null)

// Role-based colors
const primaryColor = 'secondary'
const cardColor = 'background'
const buttonColor = 'accent'
const iconColor = 'secondary'

// Filter people based on type
const filteredPeople = computed(() => {
  const assistantIds = new Set(assistants.value.map(p => p.id));
  const studentIds = new Set(students.value.map(p => p.id));

  if (personTypeToAdd.value === 'student') {
    return allPeople.value.filter(
      (person) =>
        person.type === 'STUDENT' && person.id && !studentIds.has(person.id),
    );
  } else if (personTypeToAdd.value === 'assistant') {
    return allPeople.value.filter(
      (person) =>
        (person.type === 'TEACHING_ASSISTANT') &&
        person.id && !assistantIds.has(person.id),
    );
  }
  return [];
});

const canManage = computed(() => roleStore.isMainTeacher)

const personHeaders = [
  { title: 'Nome', value: 'name', align: 'start' as const },
  { title: 'IST ID', value: 'istId', align: 'start' as const },
  { title: 'Email', value: 'email', align: 'start' as const },
  { title: 'Tipo', value: 'type', align: 'start' as const },
  { title: 'Ações', value: 'actions', sortable: false, align: 'end' as const }
]

// Updated student headers with final grade column
const studentHeaders = [
  { title: 'Nome', value: 'name', align: 'start' as const },
  { title: 'IST ID', value: 'istId', align: 'start' as const },
  { title: 'Email', value: 'email', align: 'start' as const },
  { title: 'Status', value: 'status', align: 'start' as const },
  { title: 'Nota Final', value: 'finalGrade', align: 'center' as const },
  { title: 'Ações', value: 'actions', sortable: false, align: 'end' as const }
]

onMounted(async () => {
  await fetchData()
  if (canManage.value) {
    allPeople.value = await RemoteService.getPeople()
  }
})

async function fetchData() {
  try {
    const [unitData, assistantsData, studentsData] = await Promise.all([
      RemoteService.getCurriculumUnit(unitId),
      RemoteService.getAssistants(unitId),
      RemoteService.getStudents(unitId)
    ])
    unit.value = unitData
    assistants.value = assistantsData.map(a => a.assistant)
    
    // Map enrollment data to include finalGrade
    students.value = studentsData.map((enrollment: EnrollmentDto): StudentWithGrade => ({
      ...enrollment.student,
      status: enrollment.status,
      finalGrade: enrollment.finalGrade
    }))
  } catch (error) {
    console.error('Failed to fetch personnel data:', error)
  }
}

function openAddDialog(type: 'student' | 'assistant') {
  personTypeToAdd.value = type
  selectedPerson.value = null
  addDialog.value = true
}

function openGradeBreakdown(student: StudentWithGrade) {
  selectedStudentForGrade.value = student
  gradeBreakdownDialog.value = true
}

function getGradeColor(grade: number): string {
  if (grade >= 16) return 'success'
  if (grade >= 14) return 'info'
  if (grade >= 10) return 'warning'
  return 'error'
}

async function confirmAddPerson() {
  if (!selectedPerson.value?.id) return
  try {
    if (personTypeToAdd.value === 'student') {
      await RemoteService.addStudent(unitId, selectedPerson.value.id)
    } else {
      await RemoteService.addAssistant(unitId, selectedPerson.value.id)
    }
    await fetchData() // Refresh data
  } catch (error) {
    console.error(`Failed to add ${personTypeToAdd.value}:`, error)
    alert(`Erro ao adicionar ${personTypeToAdd.value}. Verifique se a pessoa já não está associada.`)
  }
  addDialog.value = false
}

async function removePerson(person: PersonDto, type: 'student' | 'assistant') {
  if (!person.id) return
  if (confirm(`Tem a certeza que quer remover ${person.name} da unidade curricular?`)) {
    try {
      if (type === 'student') {
        await RemoteService.removeStudent(unitId, person.id)
      } else {
        await RemoteService.removeAssistant(unitId, person.id)
      }
      await fetchData() // Refresh data
    } catch (error) {
      console.error(`Failed to remove ${type}:`, error)
      alert(`Erro ao remover ${type}.`)
    }
  }
}
</script>