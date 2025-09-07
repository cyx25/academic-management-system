<!-- filepath: /home/paulo/DEI/candidatura-projeto/projeto-dei/src/frontend/src/views/curriculumunits/units/UnitLayout.vue -->
<template>
  <v-layout class="unit-layout">
    <!-- Enhanced Navigation Drawer -->
    <v-navigation-drawer 
      location="left" 
      permanent 
      width="340"
      class="modern-drawer"
      elevation="2"
    >
      <!-- Drawer Header -->
      <div class="drawer-header pa-6">
        <div class="d-flex align-center mb-3">
          <v-avatar size="56" color="primary" class="mr-4" variant="elevated">
        <v-icon icon="mdi-school-outline" color="black" size="32"></v-icon>
          </v-avatar>
          <div>
        <h3 class="text-h6 font-weight-bold mb-1">Unidade Curricular</h3>
        <p class="text-caption text-medium-emphasis mb-0">
          Gestão e navegação da unidade
        </p>
          </div>
        </div>
        <v-divider></v-divider>
      </div>

      <!-- Navigation Items -->
      <v-list nav class="px-3">
        <v-list-item
          v-for="item in navigationItems"
          :key="item.route"
          :class="['nav-item mb-1', { 'nav-item-active': isActiveRoute(item.route) }]"
          :prepend-icon="item.icon"
        
          @click="navigateTo(item.route)"
          rounded="lg"
          :color="item.color"
          :variant="isActiveRoute(item.route) ? 'tonal' : 'text'"
        >
          <template v-slot:prepend>
            <v-icon 
              :icon="item.icon" 
              :color="getIconColor(item.color, isActiveRoute(item.route))"
              size="20"
            ></v-icon>
          </template>
          
          <v-list-item-title class="font-weight-medium">
            {{ item.title }}
          </v-list-item-title>
        </v-list-item>

        <!-- Revisions Item for Teachers -->
        <v-list-item
          v-if="roleStore.isTeacher"
          :class="['nav-item mb-1', { 'nav-item-active': isActiveRoute('RevisionsView') }]"
          prepend-icon="mdi-comment-question-outline"
          @click="navigateTo('RevisionsView')"
          rounded="lg"
          color="purple"
          :variant="isActiveRoute('RevisionsView') ? 'tonal' : 'text'"
        >
          <template v-slot:prepend>
            <v-icon 
              icon="mdi-comment-question-outline" 
              :color="getIconColor('purple', isActiveRoute('RevisionsView'))"
              size="20"
            ></v-icon>
          </template>
          
          <v-list-item-title class="font-weight-medium">
            Revisões
          </v-list-item-title>

          <template v-slot:append v-if="pendingRevisionsCount > 0">
            <v-badge 
              :content="pendingRevisionsCount" 
              color="error" 
              inline 
              class="ml-2"
            />
          </template>
        </v-list-item>
      </v-list>

      <!-- Drawer Footer -->
      <template v-slot:append>
        <div class="drawer-footer pa-4">
          <v-divider class="mb-4"></v-divider>
          <div class="text-center mb-2">
        <v-chip 
          color="primary" 
          variant="elevated" 
          size="large"
          prepend-icon="mdi-calendar"
          class="mb-1"
        >
          2025/26
        </v-chip>
          </div>
          <div class="text-caption text-medium-emphasis text-center">
        Ano letivo atual
          </div>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- Main Content Area -->
    <v-main class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'

const router = useRouter()
const route = useRoute()
const roleStore = useRoleStore()
const pendingRevisionsCount = ref(0)

onMounted(async () => {
  if (roleStore.isTeacher) {
    try {
      const unitId = Number(route.params.id)
      const revisions = await RemoteService.getPendingRevisions(unitId)
      pendingRevisionsCount.value = revisions.length
    } catch (error) {
      console.error('Failed to fetch pending revisions count:', error)
    }
  }
})

interface NavigationItem {
  route: string
  title: string
  icon: string
  color: string
}

const navigationItems = computed<NavigationItem[]>(() => [
  {
    route: 'UnitView',
    title: 'Visão Geral',
    icon: 'mdi-view-dashboard',
    color: 'primary'
  },
  {
    route: 'PersonnelView',
    title: 'Pessoal',
    icon: 'mdi-account-group',
    color: 'success'
  },
  {
    route: 'ProjectsView',
    title: 'Projetos',
    icon: 'mdi-folder-multiple',
    color: 'warning'
  },
  {
    route: 'TestsView',
    title: 'Testes',
    icon: 'mdi-clipboard-check',
    color: 'error'
  },
  {
    route: 'MaterialsView',
    title: 'Materiais',
    icon: 'mdi-book-open-variant',
    color: 'info'
  },
  {
  route: 'UnitStatisticsView',
  title: 'Estatísticas',
  icon: 'mdi-chart-box',
  color: 'purple'
}])

const isActiveRoute = (routeName: string): boolean => {
  return route.name === routeName
}

const getIconColor = (itemColor: string, isActive: boolean): string => {
  if (isActive) {
    // Use the item's theme color for active state - ensures good contrast
    return itemColor
  } else {
    // Use medium emphasis for inactive state - works in both themes
    return 'medium-emphasis'
  }
}

const navigateTo = (routeName: string) => {
  const validRoutes = ['PersonnelView', 'ProjectsView', 'MaterialsView', 'UnitView', 'TestsView', 'StudentTestsView', 'RevisionsView', 'UnitStatisticsView']
  
  if (validRoutes.includes(routeName)) {
    router.push({ name: routeName, params: { id: route.params.id } })
  } else {
    console.warn(`Route ${routeName} is not implemented yet`)
  }
}
</script>

<style scoped>
.unit-layout {
  height: 100vh;
}

.modern-drawer {
  border-right: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
  background: rgb(var(--v-theme-surface));
}

.drawer-header {
  background: rgba(var(--v-theme-primary), 0.08);
  border-radius: 0 0 16px 16px;
  margin: 8px;
  border: 1px solid rgba(var(--v-theme-primary), 0.12);
  transition: background-color 0.3s ease;
}

.nav-item {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 2px 0;
  border-radius: 12px;
}

.nav-item:hover {
  transform: translateX(4px);
  background: rgba(var(--v-theme-on-surface), 0.04);
}

.nav-item-active {
  transform: translateX(4px);
  background: rgba(var(--v-theme-primary), 0.08);
  border: 1px solid rgba(var(--v-theme-primary), 0.12);
}

.nav-item-active .v-list-item-title {
  color: rgb(var(--v-theme-primary));
  font-weight: 600;
}

.main-content {
  background: rgb(var(--v-theme-background));
}

.content-wrapper {
  padding: 24px;
  max-width: 100%;
  margin: 0 auto;
  min-height: calc(100vh - 48px);
}

.drawer-footer {
  background: rgba(var(--v-theme-surface-variant), 0.5);
  border-radius: 16px;
  margin: 8px;
  border: 1px solid rgba(var(--v-theme-outline), 0.2);
  transition: background-color 0.3s ease;
}

/* Theme-specific enhancements */
.v-theme--light .drawer-header {
  background: rgba(var(--v-theme-primary), 0.05);
  border-color: rgba(var(--v-theme-primary), 0.08);
}

.v-theme--dark .drawer-header {
  background: rgba(var(--v-theme-primary), 0.12);
  border-color: rgba(var(--v-theme-primary), 0.16);
}

.v-theme--light .nav-item-active {
  background: rgba(var(--v-theme-primary), 0.06);
  border-color: rgba(var(--v-theme-primary), 0.1);
}

.v-theme--dark .nav-item-active {
  background: rgba(var(--v-theme-primary), 0.1);
  border-color: rgba(var(--v-theme-primary), 0.14);
}

.v-theme--light .drawer-footer {
  background: rgba(var(--v-theme-surface-variant), 0.3);
}

.v-theme--dark .drawer-footer {
  background: rgba(var(--v-theme-surface-variant), 0.6);
}
</style>