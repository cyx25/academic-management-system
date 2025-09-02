<template>
  <UtilBar />
  <NavBar :navbarItems="navbarItems" />
</template>

<script setup lang="ts">
import UtilBar from '@/components/UtilBar.vue'
import NavBar from '@/components/NavBar.vue'

const navbarItems = [
  { name: 'Unidades Curriculares', path: '/curriculum-units', icon: 'mdi-book-education' },
  { name: 'Cursos', path: '/courses', icon: 'mdi-school' },
  { name: 'Pessoal', path: '/people', icon: 'mdi-account-group' },
  { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' },

]
</script>
