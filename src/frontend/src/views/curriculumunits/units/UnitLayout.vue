<template>
  <v-layout class="unit-layout">
    <!-- Enhanced Navigation Drawer -->
    <v-navigation-drawer 
      location="left" 
      permanent 
      width="280"
      class="modern-drawer"
      elevation="2"
    >
      <!-- Drawer Header -->
      <div class="drawer-header pa-6">
        <div class="d-flex align-center mb-3">
          <v-avatar size="48" color="primary" class="mr-3" variant="tonal">
            <v-icon icon="mdi-school" color="primary" size="24"></v-icon>
          </v-avatar>
          <div>
            <h3 class="text-h6 font-weight-medium mb-0">Curriculum Unit</h3>
            <p class="text-caption text-medium-emphasis mb-0">Navigation</p>
          </div>
        </div>
        <v-divider></v-divider>
      </div>

      <!-- Navigation Items -->
      <v-list nav class="px-3">
        <v-list-item
          v-for="item in navigationItems"
          :key="item.name"
          :class="['nav-item mb-1', { 'nav-item-active': isActiveRoute(item.routeName) }]"
          :prepend-icon="item.icon"
          :title="item.title"
          @click="navigateTo(item.routeName)"
          rounded="lg"
          :color="item.color"
          :variant="isActiveRoute(item.routeName) ? 'flat' : 'text'"
        >
          <template v-slot:prepend>
            <v-icon 
              :icon="item.icon" 
              :color="isActiveRoute(item.routeName) ? 'white' : item.color"
              size="20"
            ></v-icon>
          </template>
          
          <v-list-item-title class="font-weight-medium">
            {{ item.title }}
          </v-list-item-title>
        </v-list-item>
      </v-list>

      <!-- Drawer Footer -->
      <template v-slot:append>
        <div class="drawer-footer pa-4">
          <v-divider class="mb-4"></v-divider>
          <div class="text-center">
            <v-chip 
              color="primary" 
              variant="tonal" 
              size="small"
              prepend-icon="mdi-calendar"
            >
              2024/25
            </v-chip>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

interface NavigationItem {
  name: string
  routeName: string
  title: string
  icon: string
  color: string
}

const navigationItems = computed<NavigationItem[]>(() => [
  {
    name: 'overview',
    routeName: 'UnitView',
    title: 'Overview',
    icon: 'mdi-view-dashboard',
    color: 'primary'
  },
  {
    name: 'personnel',
    routeName: 'PersonnelView',
    title: 'Personnel',
    icon: 'mdi-account-group',
    color: 'success'
  },
  {
    name: 'projects',
    routeName: 'ProjectsView',
    title: 'Projects',
    icon: 'mdi-folder-multiple',
    color: 'warning'
  },
  {
    name: 'tests',
    routeName: 'TestsView',
    title: 'Tests',
    icon: 'mdi-clipboard-check',
    color: 'error'
  },
  {
    name: 'materials',
    routeName: 'MaterialsView',
    title: 'Materials',
    icon: 'mdi-book-open-variant',
    color: 'info'
  }
])

const isActiveRoute = (routeName: string): boolean => {
  return route.name === routeName
}

const navigateTo = (routeName: string) => {
  if (['PersonnelView', 'ProjectsView', 'MaterialsView', 'UnitView'].includes(routeName)) {
    router.push({ name: routeName, params: { id: route.params.id } })
  } else {
    // For now, show coming soon message for unimplemented routes
    alert(`${routeName} is coming soon!`)
  }
}
</script>

<style scoped>
.unit-layout {
  height: 100vh;
}

.modern-drawer {
  border-right: 1px solid rgb(var(--v-theme-surface-variant));
}

.drawer-header {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 0 0 16px 16px;
  margin: 8px;
}

.nav-item {
  transition: all 0.2s ease;
  margin: 2px 0;
}

.nav-item:hover {
  transform: translateX(4px);
}

.nav-item-active {
  transform: translateX(4px);
}

.main-content {
  background: rgb(var(--v-theme-background));
}

.content-wrapper {
  padding: 24px;
  max-width: 100%;
  margin: 0 auto;
}

.drawer-footer {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 16px;
  margin: 8px;
}
</style>