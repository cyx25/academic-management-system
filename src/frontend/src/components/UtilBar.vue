<template>
  <v-app-bar color="secondary" :height="36" class="px-2">
    <v-toolbar-items>
      <v-btn
        href="https://dei.tecnico.ulisboa.pt/"
        selected-class="no-active"
        class="dei-title"
        size="small"
      >
        Departamento de Engenharia Informática
      </v-btn>
    </v-toolbar-items>
    <v-spacer />
    <span v-if="roleStore.isLoggedIn">
      Bem-vindo, {{ roleStore.currentUser?.name }} ({{ currentRole }})
    </span>
    <span v-else>Cargo: {{ currentRole }}</span>
    <v-spacer />
    <v-toolbar-items class="align-center">
      <DarkModeSwitch />
    </v-toolbar-items>

    <v-toolbar-items class="ms-2">
      <v-btn size="small" @click="promptLogin('student')">Aluno</v-btn>
      <v-btn size="small" @click="promptLogin('teaching_assistant')">Professor Assistente</v-btn>
      <v-btn size="small" @click="promptLogin('main_teacher')">Professor Regente</v-btn>
      <v-btn size="small" @click="handleRoleChange('administrator')">Administrador</v-btn>
    </v-toolbar-items>
    <v-toolbar-items class="ms-2">
      <v-btn size="small" @click="handleLogout" variant="text">
        Terminar sessão
        <v-icon size="small" class="ms-1" icon="mdi-logout"></v-icon>
      </v-btn>
    </v-toolbar-items>

    <LoginDialog
      v-model="loginDialog"
      :role="roleToLogin"
      :error="loginError"
      @login="handleLogin"
    />
  </v-app-bar>
</template>

<script setup lang="ts">
import DarkModeSwitch from './DarkModeSwitch.vue'
import LoginDialog from './LoginDialog.vue'
import { useRoleStore } from '@/stores/role'
import { ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'

const roleStore = useRoleStore()
const router = useRouter()

const loginDialog = ref(false)
const roleToLogin = ref('')
const loginError = ref('')

const currentRole = computed(() => roleStore.currentRole.replace('_', ' '))

const promptLogin = (role: string) => {
  roleToLogin.value = role
  loginError.value = ''
  loginDialog.value = true
}

const handleLogin = async (personId: number) => {
  const success = await roleStore.login(roleToLogin.value, personId)
  if (success) {
    loginDialog.value = false
    router.push({ name: `home` })
  } else {
    loginError.value = `ID invalido para ${roleToLogin.value}. Tente novamente.`
  }
}

const handleRoleChange = (role: string) => {
  roleStore.setRole(role)
}

const handleLogout = () => {
  roleStore.logout()
  router.push({ name: 'home' })
}

watch(
  () => roleStore.currentRole,
  (newRole) => {
    if (newRole === 'none' && router.currentRoute.value.meta.requiresAuth) {
      router.push({ name: 'home' })
    }
  }
)
</script>

<style scoped>
.dei-title:hover {
  background-color: transparent !important;
}
</style>