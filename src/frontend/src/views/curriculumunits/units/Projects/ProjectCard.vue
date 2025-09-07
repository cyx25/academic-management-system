<template>
  <v-card class="mx-auto hover-card" outlined elevation="2">
    <v-card-title class="d-flex justify-space-between">
      <span>{{ project.title }}</span>
      <v-chip size="small" :color="project.weight > 0 ? 'blue' : 'grey'">
        Peso Nota Final: {{ project.weight }}%
      </v-chip>
    </v-card-title>
    <v-card-subtitle>
      Entrega: {{ new Date(project.dueDate).toLocaleString() }}
    </v-card-subtitle>
    <v-card-text>
      <div>Tamanho dos grupos: {{ project.maxGroupSize }}</div>
    </v-card-text>
    <v-card-actions>
      <v-btn
        v-if="project.statementFile"
        color="secondary"
        text
        @click="viewStatement"
        prepend-icon="mdi-file-eye-outline"
      >
        ENUNCIADO
      </v-btn>
      <v-btn
        color="info"
        text
        :to="{ name: 'project-submissions', params: { projectId: project.id } }"
       
        prepend-icon="mdi-upload-multiple"
      >
        Submissões
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn
        v-if="roleStore.isMainTeacher"
        icon="mdi-pencil"
        variant="text"
        color="primary"
        @click="$emit('edit', project)"
      ></v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type ProjectDto from '@/models/ProjectDto';
import { useRoleStore } from '@/stores/role';
import RemoteService from '@/services/RemoteService';


const props = defineProps<{
  project: ProjectDto;
}>();

defineEmits(['edit', 'update']);

const roleStore = useRoleStore();


function viewStatement() {
  if (props.project.id) {
    const url = RemoteService.getProjectStatementUrl(props.project.id);
    window.open(url, '_blank');
  }
}
</script>
<style scoped>
.v-card {
  transition: all 0.3s ease;
  background: rgba(var(--v-theme-surface), 0.9);
  backdrop-filter: blur(10px);
}

.hover-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>