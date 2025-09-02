<template>
  <v-dialog v-model="dialog" max-width="600">
    <template v-slot:activator="{ props }" v-if="mode === 'create'">
      <v-btn
        class="text-none font-weight-regular"
        prepend-icon="mdi-plus"
        text="Adicionar Pessoa"
        v-bind="props"
        color="primary"
      ></v-btn>
    </template>

    <v-card>
      <v-card-title>{{ mode === 'create' ? 'Nova Pessoa' : 'Editar Pessoa' }}</v-card-title>
      <v-card-text>
        <v-text-field label="Nome*" v-model="person.name" required></v-text-field>
        <v-text-field label="IST ID*" v-model="person.istId" required></v-text-field>
        <v-text-field label="Email" v-model="person.email"></v-text-field>
        <v-select
          label="Tipo*"
          v-model="person.type"
          :items="['ADMINISTRATOR', 'MAIN_TEACHER', 'TEACHING_ASSISTANT', 'STUDENT']"
          required
        ></v-select>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Cancelar" @click="dialog = false"></v-btn>
        <v-btn color="primary" text="Salvar" @click="savePerson"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type PersonDto from '@/models/PersonDto'
import RemoteService from '@/services/RemoteService'

interface Props {
  mode: 'create' | 'edit'
  modelValue: boolean
  person?: PersonDto
}

const props = withDefaults(defineProps<Props>(), {
  person: () => ({ id: 0, name: '', istId: '', email: '', type: '' })
})

const emit = defineEmits(['update:modelValue', 'person-saved'])

const dialog = ref(false)
const person = ref<PersonDto>({ ...props.person })

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
  if (newVal) {
    person.value = { ...props.person }
  }
})

watch(dialog, (newVal) => {
  emit('update:modelValue', newVal)
})

const savePerson = async () => {
  if (props.mode === 'create') {
    await RemoteService.createPerson(person.value)
  } else {
    await RemoteService.updatePerson(person.value.id, person.value)
  }
  emit('person-saved')
  dialog.value = false
}
</script>