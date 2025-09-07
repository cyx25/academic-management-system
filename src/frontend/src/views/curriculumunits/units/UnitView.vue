<template>
  <v-container fluid class="pa-6">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <p class="text-h6 mt-4">Loading curriculum unit...</p>
    </div>

    <!-- Unit Information -->
    <div v-else-if="curriculumUnit">
      <!-- Header Section -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-card class="pa-6 mb-4" elevation="3">
        <div class="d-flex align-center mb-4">
          <v-avatar color="primary" size="72" class="mr-5 elevation-2">
            <v-icon icon="mdi-school" size="36"></v-icon>
          </v-avatar>
          <div>
            <h1 class="text-h3 font-weight-bold text-primary mb-1">
          {{ curriculumUnit.name }}
            </h1>
            <div class="d-flex align-center mb-2">
          <v-chip color="primary" variant="tonal" size="large" class="mr-2">
            {{ curriculumUnit.ects }} ECTS
          </v-chip>
          <v-chip
            v-if="curriculumUnit.semester"
            color="success"
            variant="outlined"
            size="small"
            class="mr-2"
          >
            {{ curriculumUnit.semester }}º Semestre
          </v-chip>
          <span class="text-h6 text-medium-emphasis">
            {{ curriculumUnit.course?.name }}
          </span>
            </div>
        
          </div>
        </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Quick Actions -->
      <v-row class="mb-6">
        <v-col cols="12">
          <h2 class="text-h5 font-weight-bold mb-4">Quick Actions</h2>
          <v-row>
            <!-- Tests Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'TestsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-clipboard-check" size="48" color="primary" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Testes</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'Ver resultados dos testes' : 'Gerir testes e classificações' }}
                </p>
              </v-card>
            </v-col>

            <!-- Projects Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'ProjectsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-folder-outline" size="48" color="secondary" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Projetos</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'Submit your assignments' : 'Manage project assignments' }}
                </p>
              </v-card>
            </v-col>

            <!-- Materials Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'MaterialsView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-book-open-outline" size="48" color="success" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Materiais</h3>
                <p class="text-body-2 text-medium-emphasis">
                  {{ roleStore.isStudent ? 'Access course materials' : 'Organize learning resources' }}
                </p>
              </v-card>
            </v-col>

            <!-- Personnel Card -->
            <v-col cols="12" sm="6" md="3">
              <v-card 
                class="pa-4 text-center hover-card" 
                elevation="2"
                :to="{ name: 'PersonnelView', params: { id: $route.params.id } }"
              >
                <v-icon icon="mdi-account-group" size="48" color="warning" class="mb-3"></v-icon>
                <h3 class="text-h6 font-weight-bold mb-2">Pessoal</h3>
                <p class="text-body-2 text-medium-emphasis">
                  Ver professores e alunos inscritos
                </p>
              </v-card>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

     
     

    </div>
     

    <!-- Error State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="error" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-alert-circle" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">Failed to Load Unit</h3>
          <p class="text-body-1">
            Unable to load curriculum unit information. Please try again.
          </p>
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type {CurriculumUnitDto, CurriculumUnitStatisticsDto, GradeDistributionDto } from '@/models/CurriculumUnitDto'


const route = useRoute()
const roleStore = useRoleStore()

const curriculumUnit = ref<CurriculumUnitDto | null>(null)
const loading = ref(true)


const unitId = computed(() => Number(route.params.id))

onMounted(async () => {
  await loadUnitData()
})





async function loadUnitData() {
  try {
    loading.value = true
    curriculumUnit.value = await RemoteService.getCurriculumUnit(unitId.value)
 
  } catch (error) {
    console.error('Failed to load unit data:', error)
  } finally {
    loading.value = false
  }
}


</script>

<style scoped>
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.hover-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.hover-card:hover .v-icon {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}
</style>