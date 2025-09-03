<template>
  <v-layout>
    <v-main>
      <router-view />
    </v-main>

    <v-navigation-drawer location="left" permanent>
      <v-list nav>
        <v-list-item
          class="text-h4"
          prepend-icon="mdi-account-group"
          title="Pessoal"
          @click="navigateTo('PersonnelView')"
        ></v-list-item>
        <v-list-item
          class="text-h4"
          prepend-icon="mdi-folder-multiple"
          title="Projetos"
          @click="navigateTo('ProjectsView')"
        ></v-list-item>
        <v-list-item
          class="text-h4"
          prepend-icon="mdi-clipboard-check"
          title="Testes"
          @click="navigateTo('TestsView')"
        ></v-list-item>
        <v-list-item
          class="text-h4"
          prepend-icon="mdi-book-open-variant"
          title="Recursos"
          @click="navigateTo('ResourcesView')"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>
  </v-layout>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const navigateTo = (routeName: string) => {
  // Placeholder for future navigation
  if (routeName === 'PersonnelView') {
    router.push({ name: routeName, params: { id: route.params.id } })
  } else {
    alert(`Navigation to ${routeName} is not implemented yet.`)
  }
}


</script>

