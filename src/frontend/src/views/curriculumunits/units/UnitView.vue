<template>
  <v-container fluid class="unit-overview pa-6">
    <!-- Hero Section -->
    <v-row class="mb-6">
      <v-col cols="12">
        <v-card elevation="12" class="hero-card overflow-hidden">
          <div class="hero-background">
            <div class="hero-content pa-8">
              <div class="d-flex align-center mb-4">
                <v-avatar size="72" color="white" class="mr-6 elevation-4">
                  <v-icon icon="mdi-school" color="primary" size="36"></v-icon>
                </v-avatar>
                <div>
                  <h1 class="text-h3 font-weight-bold text-white mb-2">
                    {{ unitTitle }}
                  </h1>
                  <p class="text-h6 text-blue-lighten-3 mb-0">
                    Academic Year 2024/25 • {{ semester }}
                  </p>
                </div>
              </div>
              
              <div class="hero-stats d-flex flex-wrap gap-4">
                <v-chip 
                  size="large" 
                  color="white" 
                  text-color="primary"
                  prepend-icon="mdi-account-group"
                  class="font-weight-bold"
                >
                  {{ enrolledStudents }} Students
                </v-chip>
                <v-chip 
                  size="large" 
                  color="white" 
                  text-color="primary"
                  prepend-icon="mdi-account-tie"
                  class="font-weight-bold"
                >
                  {{ teachersCount }} Teachers
                </v-chip>
                <v-chip 
                  size="large" 
                  color="white" 
                  text-color="primary"
                  prepend-icon="mdi-folder-multiple"
                  class="font-weight-bold"
                >
                  {{ projectsCount }} Projects
                </v-chip>
              </div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Quick Actions & Stats -->
    <v-row class="mb-6">
      <!-- Quick Actions -->
      <v-col cols="12" md="8">
        <v-card elevation="8" class="h-100">
          <v-card-title class="pa-6 pb-4">
            <div class="d-flex align-center">
              <v-icon icon="mdi-lightning-bolt" color="amber" size="24" class="mr-3"></v-icon>
              <h2 class="text-h5 font-weight-bold">Quick Actions</h2>
            </div>
          </v-card-title>
          
          <v-card-text class="pa-6 pt-0">
            <v-row>
              <v-col v-for="action in quickActions" :key="action.name" cols="6" sm="4">
                <v-card 
                  :color="action.color"
                  variant="tonal"
                  class="action-card text-center pa-4"
                  @click="navigateToAction(action.route)"
                  :class="{ 'action-disabled': action.disabled }"
                >
                  <v-icon 
                    :icon="action.icon" 
                    :color="action.color"
                    size="32" 
                    class="mb-3"
                  ></v-icon>
                  <h4 class="text-h6 font-weight-bold mb-1">{{ action.title }}</h4>
                  <p class="text-body-2 text-grey-darken-1 mb-0">{{ action.description }}</p>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Unit Stats -->
      <v-col cols="12" md="4">
        <v-card elevation="8" class="h-100 stats-card">
          <v-card-title class="pa-6 pb-4">
            <div class="d-flex align-center">
              <v-icon icon="mdi-chart-line" color="primary" size="24" class="mr-3"></v-icon>
              <h2 class="text-h5 font-weight-bold">Unit Statistics</h2>
            </div>
          </v-card-title>
          
          <v-card-text class="pa-6 pt-0">
            <div class="stats-list">
              <div v-for="stat in unitStats" :key="stat.label" class="stat-item mb-4">
                <div class="d-flex justify-space-between align-center">
                  <div class="d-flex align-center">
                    <v-icon :icon="stat.icon" :color="stat.color" size="20" class="mr-3"></v-icon>
                    <span class="font-weight-medium">{{ stat.label }}</span>
                  </div>
                  <div class="text-right">
                    <div class="text-h6 font-weight-bold" :class="`text-${stat.color}`">
                      {{ stat.value }}
                    </div>
                    <div v-if="stat.change" class="text-caption" :class="stat.changeColor">
                      {{ stat.change }}
                    </div>
                  </div>
                </div>
                <v-progress-linear
                  v-if="stat.progress !== undefined"
                  :model-value="stat.progress"
                  :color="stat.color"
                  height="4"
                  rounded
                  class="mt-2"
                ></v-progress-linear>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    
  </v-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// Mock data - replace with actual API calls
const unitTitle = computed(() => "Advanced Software Engineering")
const semester = computed(() => "Fall Semester")
const enrolledStudents = computed(() => 42)
const teachersCount = computed(() => 3)
const projectsCount = computed(() => 5)

const quickActions = computed(() => [
  {
    name: 'view-projects',
    title: 'Projects',
    description: 'View & manage projects',
    icon: 'mdi-folder-multiple',
    color: 'primary',
    route: 'ProjectsView',
    disabled: false
  },
  {
    name: 'view-personnel',
    title: 'Personnel',
    description: 'Teachers & students',
    icon: 'mdi-account-group',
    color: 'green',
    route: 'PersonnelView',
    disabled: false
  },
  {
    name: 'view-materials',
    title: 'Materials',
    description: 'Course resources',
    icon: 'mdi-book-open-variant',
    color: 'purple',
    route: 'MaterialsView',
    disabled: false
  },
  {
    name: 'view-tests',
    title: 'Tests',
    description: 'Exams & quizzes',
    icon: 'mdi-clipboard-check',
    color: 'red',
    route: 'TestsView',
    disabled: true
  },
  {
    name: 'analytics',
    title: 'Analytics',
    description: 'Performance data',
    icon: 'mdi-chart-bar',
    color: 'orange',
    route: 'AnalyticsView',
    disabled: true
  },
  {
    name: 'settings',
    title: 'Settings',
    description: 'Unit configuration',
    icon: 'mdi-cog',
    color: 'grey',
    route: 'SettingsView',
    disabled: true
  }
])

const unitStats = computed(() => [
  {
    label: 'Active Projects',
    value: '3',
    icon: 'mdi-folder-open',
    color: 'primary',
    progress: 60,
    change: '+2 this week',
    changeColor: 'text-success'
  },
  {
    label: 'Pending Submissions',
    value: '12',
    icon: 'mdi-clock-outline',
    color: 'orange',
    progress: 30,
    change: '-5 since yesterday',
    changeColor: 'text-success'
  },
  {
    label: 'Average Grade',
    value: '16.5',
    icon: 'mdi-star',
    color: 'success',
    progress: 82,
    change: '+0.8 points',
    changeColor: 'text-success'
  },
  {
    label: 'Completion Rate',
    value: '89%',
    icon: 'mdi-check-circle',
    color: 'success',
    progress: 89
  }
])



const navigateToAction = (routeName: string) => {
  if (['PersonnelView', 'ProjectsView', 'MaterialsView'].includes(routeName)) {
    router.push({ name: routeName, params: { id: route.params.id } })
  } else {
    alert(`${routeName} is coming soon!`)
  }
}
</script>

<style scoped>
.unit-overview {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.hero-card {
  position: relative;
  overflow: hidden;
}

.hero-background {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.hero-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.action-card:hover:not(.action-disabled) {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.action-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.stats-card {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
}

.stat-item {
  padding: 12px 0;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.stat-item:last-child {
  border-bottom: none;
}

.activity-card {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
}

.activity-item {
  border-bottom: 1px solid rgba(0,0,0,0.05);
  transition: background-color 0.2s ease;
}

.activity-item:hover {
  background-color: rgba(25, 118, 210, 0.04);
}

.activity-item:last-child {
  border-bottom: none;
}

.announcements-card {
  background: linear-gradient(135deg, #fff 0%, #fafafa 100%);
}

.announcement-item {
  position: relative;
}

.announcement-content {
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
  border-left: 4px solid #1976d2;
  transition: all 0.2s ease;
}

.announcement-content:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.hero-stats .v-chip {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.9) !important;
}

/* Responsive adjustments */
@media (max-width: 960px) {
  .hero-content {
    padding: 2rem !important;
  }
  
  .hero-stats {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .hero-stats .v-chip {
    margin-bottom: 0.5rem;
  }
}
</style>