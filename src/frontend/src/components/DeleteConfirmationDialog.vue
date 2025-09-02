<template>
  <v-dialog v-model="dialog" max-width="400" persistent>
    <v-card>
      <v-card-title class="text-h6">{{ title }}</v-card-title>
      <v-card-text>{{ message }}</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="cancel">Cancel</v-btn>
        <v-btn color="error" @click="confirm">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">

import { computed } from 'vue'

interface Props {
  modelValue: boolean
  title: string
  message: string
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'confirm'])

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const cancel = () => {
  dialog.value = false
}

const confirm = () => {
  emit('confirm')
  dialog.value = false
}
</script>