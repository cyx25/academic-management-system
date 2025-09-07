<!-- filepath: /home/paulo/DEI/candidatura-projeto/projeto-dei/src/frontend/src/components/TopBar.vue -->
<template>
  <UtilBar />
  <NavBar :navbarItems="navbarItems" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoleStore } from '@/stores/role'
import UtilBar from '@/components/UtilBar.vue'
import NavBar from '@/components/NavBar.vue'

const roleStore = useRoleStore()

// Base navigation items available to all roles
const baseNavItems = [
  { name: 'Início', path: '/', icon: 'mdi-home' },
  { name: 'Unidades Curriculares', path: '/curriculum-units', icon: 'mdi-book-education' },
  { name: 'Cursos', path: '/courses', icon: 'mdi-school' }
]

// Role-specific navigation items
const roleSpecificItems = {
  administrator: [
    { name: 'Pessoal', path: '/people', icon: 'mdi-account-group' }
  ],
  student: [
    { name: 'Perfil', path: '/profile', icon: 'mdi-account-circle' },
    { name: 'Prazos', path: '/deadlines', icon: 'mdi-calendar-clock' },
    { name: 'Progresso', path: '/progress', icon: 'mdi-chart-line' }
  ],
  main_teacher: [
    { name: 'Alunos', path: '/students', icon: 'mdi-account-school' },
    { name: 'Correções Pendentes', path: '/pending-corrections', icon: 'mdi-clipboard-check-outline' },
    { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' }
  ],
  teaching_assistant: [
    { name: 'Tarefas de Correção', path: '/correction-tasks', icon: 'mdi-clipboard-edit' },
    { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' }
  ]
}

// Computed property that combines base items with role-specific items
const navbarItems = computed(() => {
  const currentRole = roleStore.currentRole
  const specificItems = roleSpecificItems[currentRole as keyof typeof roleSpecificItems] || []
  
  return [
    ...baseNavItems,
    ...specificItems
  ]
})
</script>