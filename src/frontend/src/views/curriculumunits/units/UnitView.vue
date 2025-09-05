<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card elevation="4" class="pa-6">
          <v-card-title class="text-h4 mb-4">
            Detalhes da Unidade Curricular
          </v-card-title>

          <v-card-text>
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  label="Nome"
                  :model-value="curriculumUnit?.name"
                  readonly
                  variant="outlined"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  label="Código"
                  :model-value="curriculumUnit?.code"
                  readonly
                  variant="outlined"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  label="Semestre"
                  :model-value="curriculumUnit?.semester"
                  readonly
                  variant="outlined"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  label="ECTS"
                  :model-value="curriculumUnit?.ects"
                  readonly
                  variant="outlined"
                ></v-text-field>
              </v-col>
            </v-row>

           
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute} from 'vue-router'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import RemoteService from '@/services/RemoteService'

const route = useRoute()

const curriculumUnit = ref<CurriculumUnitDto | null>(null)

onMounted(async () => {
  const id = route.params.id as string
  if (id) {
    try {
      curriculumUnit.value = await RemoteService.getCurriculumUnit(parseInt(id))
    } catch (error) {
      console.error('Error fetching curriculum unit:', error)
      // Handle error (e.g., redirect to list or show error message)
    }
  }
})

</script>