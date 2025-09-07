<template>
  <v-container fluid class="home-container">
    <!-- None Role - Sign In Required -->
    <div v-if="roleStore.currentRole === 'none'" class="text-center">
      <v-card class="mx-auto pa-8" max-width="600" elevation="8" rounded="lg">
        <v-icon icon="mdi-account-key" size="64" color="primary" class="mb-4"></v-icon>
        <h1 class="text-h3 font-weight-bold text-primary mb-4">
          Bem-vindo ao {{ appName }}
        </h1>
        <h2 class="text-h5 text-medium-emphasis mb-6">
          Por favor, inicie sessão para continuar
        </h2>
        <p class="text-body-1 text-medium-emphasis">
          Selecione um cargo na barra de navegação superior para aceder ao Sistema de Gestão Académica.
        </p>
      </v-card>
    </div>

    <!-- Logged In Roles -->
    <div v-else>
      <!-- Header Section -->
      <v-row class="mb-8">
        <v-col cols="12">
          <v-card class="pa-8 text-center" elevation="6" rounded="lg" :color="getRoleColor()" variant="tonal">
            <v-avatar :color="getRoleColor()" size="80" class="mb-4 elevation-4">
              <v-icon :icon="getRoleIcon()" size="40" color="white"></v-icon>
            </v-avatar>
            <h1 class="text-h3 font-weight-bold mb-2" :class="`text-${getRoleColor()}`">
              {{ getWelcomeMessage() }}
            </h1>
            <h2 class="text-h5 text-medium-emphasis mb-4">
              {{ getRoleDescription() }}
            </h2>
            <p class="text-body-1 text-medium-emphasis">
              {{ getSubDescription() }}
            </p>
          </v-card>
        </v-col>
      </v-row>

      <!-- Role-Specific Capabilities -->
      <v-row class="mb-8">
        <v-col cols="12">
          <h2 class="text-h4 font-weight-bold text-center mb-6">As Suas Capacidades</h2>
          <v-row>
            <v-col
              v-for="capability in getRoleCapabilities()"
              :key="capability.title"
              cols="12"
              md="6"
              lg="4"
            >
              <v-card 
                class="pa-6 h-100 capability-card" 
                elevation="4" 
                rounded="lg"
                :color="capability.color"
                variant="tonal"
              >
                <div class="text-center">
                  <v-icon 
                    :icon="capability.icon" 
                    :color="capability.color" 
                    size="48" 
                    class="mb-4"
                  ></v-icon>
                  <h3 class="text-h6 font-weight-bold mb-3" :class="`text-${capability.color}`">
                    {{ capability.title }}
                  </h3>
                  <p class="text-body-2 text-medium-emphasis">
                    {{ capability.description }}
                  </p>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <!-- Quick Actions -->
      <v-row v-if="getQuickActions().length > 0">
        <v-col cols="12">
          <h2 class="text-h4 font-weight-bold text-center mb-6">Ações Rápidas</h2>
          <v-row justify="center">
            <v-col
              v-for="action in getQuickActions()"
              :key="action.title"
              cols="12"
              sm="6"
              md="4"
            >
              <v-card 
                class="pa-4 text-center action-card" 
                elevation="2" 
                rounded="lg"
                :to="action.path"
                hover
              >
                <v-icon :icon="action.icon" :color="action.color" size="32" class="mb-3"></v-icon>
                <h4 class="text-h6 font-weight-bold mb-2">{{ action.title }}</h4>
                <p class="text-body-2 text-medium-emphasis">{{ action.description }}</p>
              </v-card>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup lang="ts">

import { useRoleStore } from '@/stores/role'

const appName = import.meta.env.VITE_NAME
const roleStore = useRoleStore()

const getRoleColor = () => {
  const colors = {
    administrator: 'red',
    main_teacher: 'blue',
    teaching_assistant: 'green',
    student: 'purple'
  }
  return colors[roleStore.currentRole as keyof typeof colors] || 'primary'
}

const getRoleIcon = () => {
  const icons = {
    administrator: 'mdi-shield-crown',
    main_teacher: 'mdi-account-tie',
    teaching_assistant: 'mdi-account-supervisor',
    student: 'mdi-school'
  }
  return icons[roleStore.currentRole as keyof typeof icons] || 'mdi-account'
}

const getWelcomeMessage = () => {
  const messages = {
    administrator: `Bem-vindo, Administrador!`,
    main_teacher: `Bem-vindo, ${roleStore.currentUser?.name || 'Professor'}!`,
    teaching_assistant: `Bem-vindo, ${roleStore.currentUser?.name || 'Assistente'}!`,
    student: `Bem-vindo, ${roleStore.currentUser?.name || 'Estudante'}!`
  }
  return messages[roleStore.currentRole as keyof typeof messages] || 'Bem-vindo!'
}

const getRoleDescription = () => {
  const descriptions = {
    administrator: 'Administrador do Sistema',
    main_teacher: 'Professor Regente - Líder do Curso',
    teaching_assistant: 'Professor Assistente',
    student: 'Estudante'
  }
  return descriptions[roleStore.currentRole as keyof typeof descriptions] || ''
}

const getSubDescription = () => {
  const descriptions = {
    administrator: 'Gere todo o sistema académico com privilégios administrativos completos.',
    main_teacher: 'Lidera as suas unidades curriculares e gere todas as avaliações académicas.',
    teaching_assistant: 'Apoia as atividades de ensino e auxilia nas avaliações dos estudantes.',
    student: 'Acede aos seus cursos, consulte as suas notas e submeta trabalhos.'
  }
  return descriptions[roleStore.currentRole as keyof typeof descriptions] || ''
}

const getRoleCapabilities = () => {
  const capabilities = {
    administrator: [
      {
        title: 'Gestão do Sistema',
        description: 'Criar, atualizar e remover unidades curriculares, cursos e pessoas.',
        icon: 'mdi-cog',
        color: 'red'
      },
      {
        title: 'Gestão de Utilizadores',
        description: 'Gerir todas as pessoas no sistema incluindo estudantes, professores e assistentes.',
        icon: 'mdi-account-group',
        color: 'orange'
      },
      {
        title: 'Supervisão de Cursos',
        description: 'Ver e gerir todos os cursos e unidades curriculares da instituição.',
        icon: 'mdi-school',
        color: 'blue'
      }
    ],
    main_teacher: [
      {
        title: 'Liderança do Curso',
        description: 'Gerir as suas unidades curriculares como professor regente responsável.',
        icon: 'mdi-teach',
        color: 'blue'
      },
      {
        title: 'Gestão de Equipas',
        description: 'Associar assistentes de ensino e estudantes às suas unidades curriculares.',
        icon: 'mdi-account-supervisor-circle',
        color: 'green'
      },
      {
        title: 'Controlo de Avaliações',
        description: 'Criar testes e projetos, atribuir notas e gerir pedidos de revisão.',
        icon: 'mdi-clipboard-check',
        color: 'purple'
      },
      {
        title: 'Gestão de Recursos',
        description: 'Carregar e organizar materiais de aprendizagem, enunciados de testes e diretrizes de projetos.',
        icon: 'mdi-folder-open',
        color: 'orange'
      }
    ],
    teaching_assistant: [
      {
        title: 'Apoio à Classificação',
        description: 'Atribuir notas aos estudantes em testes e projetos sob supervisão do professor.',
        icon: 'mdi-pencil',
        color: 'green'
      },
      {
        title: 'Tarefas de Avaliação',
        description: 'Realizar tarefas de classificação e apoiar professores regentes na gestão de avaliações.',
        icon: 'mdi-clipboard-list',
        color: 'blue'
      },
      {
        title: 'Análise de Revisões',
        description: 'Rever pedidos de revisão de estudantes e fornecer recomendações iniciais.',
        icon: 'mdi-file-document-edit',
        color: 'purple'
      }
    ],
    student: [
      {
        title: 'Acesso a Notas',
        description: 'Ver as suas notas em testes e projetos em todas as unidades curriculares inscritas.',
        icon: 'mdi-chart-line',
        color: 'purple'
      },
      {
        title: 'Submissão de Projetos',
        description: 'Submeter projetos individuais e de grupo com capacidades de carregamento de ficheiros.',
        icon: 'mdi-upload',
        color: 'blue'
      },
      {
        title: 'Pedidos de Revisão',
        description: 'Submeter pedidos de revisão de testes com justificação dentro dos prazos limite.',
        icon: 'mdi-comment-question',
        color: 'orange'
      },
      {
        title: 'Progresso Académico',
        description: 'Acompanhar o seu progresso e médias finais baseadas nos pesos de testes e projetos.',
        icon: 'mdi-progress-check',
        color: 'green'
      }
    ]
  }
  return capabilities[roleStore.currentRole as keyof typeof capabilities] || []
}

const getQuickActions = () => {
  const actions = {
    administrator: [
      {
        title: 'Gerir Pessoas',
        description: 'Adicionar e gerir utilizadores do sistema',
        icon: 'mdi-account-group',
        color: 'primary',
        path: '/people'
      },
      {
        title: 'Unidades Curriculares',
        description: 'Gerir unidades académicas',
        icon: 'mdi-book-education',
        color: 'blue',
        path: '/curriculum-units'
      },
      {
        title: 'Cursos',
        description: 'Gerir catálogo de cursos',
        icon: 'mdi-school',
        color: 'green',
        path: '/courses'
      }
    ],
    main_teacher: [
      {
        title: 'Os Meus Estudantes',
        description: 'Ver estudantes das suas unidades',
        icon: 'mdi-account-school',
        color: 'blue',
        path: '/students'
      },
      {
        title: 'Tarefas Pendentes',
        description: 'Tratar tarefas de correção',
        icon: 'mdi-clipboard-check-outline',
        color: 'orange',
        path: '/pending-corrections'
      },
      {
        title: 'Estatísticas',
        description: 'Ver estatísticas de ensino',
        icon: 'mdi-chart-bar',
        color: 'purple',
        path: '/main-statistics'
      }
    ],
    teaching_assistant: [
      {
        title: 'Tarefas de Classificação',
        description: 'Completar classificações atribuídas',
        icon: 'mdi-clipboard-edit',
        color: 'green',
        path: '/correction-tasks'
      },
      {
        title: 'Estatísticas',
        description: 'Ver estatísticas de classificação',
        icon: 'mdi-chart-bar',
        color: 'blue',
        path: '/assistant-statistics'
      }
    ],
    student: [
      {
        title: 'O Meu Perfil',
        description: 'Ver notas e progresso',
        icon: 'mdi-account-circle',
        color: 'purple',
        path: '/profile'
      },
      {
        title: 'Prazos',
        description: 'Verificar prazos próximos',
        icon: 'mdi-calendar-clock',
        color: 'orange',
        path: '/deadlines'
      },
      {
        title: 'Progresso',
        description: 'Acompanhar progresso académico',
        icon: 'mdi-chart-line',
        color: 'green',
        path: '/progress'
      }
    ]
  }
  return actions[roleStore.currentRole as keyof typeof actions] || []
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(var(--v-theme-surface), 0.8) 0%, rgba(var(--v-theme-background), 0.9) 100%);
  padding: 2rem;
}

.capability-card {
  transition: all 0.3s ease;
  cursor: default;
}

.capability-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.action-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.action-card:hover .v-icon {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}
</style>