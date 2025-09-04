<template>
  <v-dialog v-model="dialog" max-width="400px" persistent>
    <v-card>
      <v-card-title>
        <span class="text-h5">Login as {{ roleName }}</span>
      </v-card-title>
      <v-card-text>
        <v-text-field
          v-model="personId"
          label="Insert ID"
          type="text"
          inputmode="numeric"
          autofocus
          @keyup.enter="submit"
        ></v-text-field>
        <v-alert v-if="error" type="error" dense>{{ error }}</v-alert>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="close">Cancel</v-btn>
        <v-btn color="primary" @click="submit">Login</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  role: { type: String, required: true },
  error: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue', 'login'])

const personId = ref<number | null>(null)

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const roleName = computed(() => {
  return props.role.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
})

const close = () => {
  dialog.value = false
}

const submit = () => {
  if (personId.value) {
    emit('login', personId.value)
  }
}

watch(dialog, (newValue) => {
  if (newValue) {
    personId.value = null
  }
})
</script>