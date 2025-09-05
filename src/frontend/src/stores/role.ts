import { defineStore } from 'pinia'
import type PersonDto from '@/models/PersonDto'
import RemoteService from '@/services/RemoteService'

export const useRoleStore = defineStore('role', {
  state: () => ({
    currentRole: 'none',
    currentUser: null as PersonDto | null,
  }),
  getters: {
    isLoggedIn: (state) => state.currentRole !== 'none' && state.currentUser !== null,
    isAdministrator: (state) => state.currentRole === 'administrator',
    isMainTeacher: (state) => state.currentRole === 'main_teacher',
    isStudent: (state) => state.currentRole === 'student',
    isTeachingAssistant: (state) => state.currentRole === 'teaching_assistant',
    isTeacher: (state) => state.currentRole === 'main_teacher' || state.currentRole === 'teaching_assistant',
    getCurrentUserID: (state) => state.currentUser ? state.currentUser.id : null,
  },
  actions: {
    async login(role: string, personId: number): Promise<boolean> {


      if (personId < 0) {
        console.error('Invalid person ID:', personId)
        return false
      }

      const roleToPersonType: { [key: string]: string } = {
        student: 'STUDENT',
        teaching_assistant: 'TEACHING_ASSISTANT',
        main_teacher: 'MAIN_TEACHER',
      }
      const personType = roleToPersonType[role]

      try {
        const people = await RemoteService.getPeople()
        const person = people.find(
          (p) => p.id == personId && p.type === personType,
        )

        if (person) {
          this.currentRole = role
          this.currentUser = person
          return true
        } else {
          console.error('No person found:', personId)
          this.logout()
          return false
        }
      } catch (error) {
        console.error('Login failed:', error)
        this.logout()
        return false
      }
    },
    logout() {
      this.currentRole = 'none'
      this.currentUser = null
    },
    setRole(role: string) {
      // For roles without login, like administrator
      if (role === 'administrator' || role === 'none') {
        this.logout()
        this.currentRole = role
      }
    },
  },
})