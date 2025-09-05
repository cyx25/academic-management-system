<template>
  <v-dialog v-model="dialog" max-width="600px" persistent>
    <v-card>
      <v-card-title>
        <span class="text-h5">{{ formTitle }}</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-text-field
            v-model="editableProject.title"
            label="Title*"
            :rules="[rules.required]"
            :disabled="isEdit"
          ></v-text-field>
          <v-text-field
            v-model.number="editableProject.weight"
            label="Weight (%)*"
            type="number"
            :rules="[rules.required, rules.number, rules.percentage]"
          ></v-text-field>
          <v-text-field
            v-model="editableProject.dueDate"
            label="Due Date*"
            type="datetime-local"
            :rules="[rules.required]"
          ></v-text-field>
          <v-text-field
            v-model.number="editableProject.maxGroupSize"
            label="Max Group Size*"
            type="number"
            :rules="[rules.required, rules.number, rules.minOne]"
            :disabled="isEdit"
          ></v-text-field>
          
          <!-- Replace v-file-input with standard HTML file input -->
          <div class="mb-3">
            <label for="statement-file" class="text-subtitle-1 d-block mb-1">Statement File*</label>
            <input
              id="statement-file"
              type="file"
              @change="handleFileChange"
              class="file-input"
              :required="!isEdit"
            />
            <div v-if="selectedFile" class="text-caption mt-1">
              Selected: {{ selectedFile.name }} ({{ formatFileSize(selectedFile.size) }})
            </div>
            <div v-if="isEdit && editableProject.statementFile" class="text-caption mt-1">
              Current file: {{ editableProject.statementFile.fileName }}
            </div>
          </div>
          
          <small v-if="isEdit">Uploading a new file will replace the existing one.</small>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="close">Cancel</v-btn>
        <v-btn color="primary" @click="save" :disabled="!valid">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import type ProjectDto from '@/models/ProjectDto';
import RemoteService from '@/services/RemoteService';

const props = defineProps<{
  modelValue: boolean;
  project: ProjectDto | null;
  isEdit: boolean;
}>();

const emit = defineEmits(['update:modelValue', 'save']);

const route = useRoute();
const unitId = Number(route.params.id);

const form = ref<any>(null);
const valid = ref(true);
const editableProject = ref<ProjectDto>({} as ProjectDto);
const selectedFile = ref<File | null>(null);

const dialog = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const formTitle = computed(() => (props.isEdit ? 'Edit Project' : 'Create Project'));

const rules = {
  required: (v: any) => !!v || 'Field is required',
  number: (v: any) => !isNaN(parseFloat(v)) || 'Must be a number',
  percentage: (v: number) => (v > 0 && v <= 100) || 'Must be between 1 and 100',
  minOne: (v: number) => v >= 1 || 'Must be at least 1',
};

watch(
  () => props.project,
  (newVal) => {
    if (newVal) {
      editableProject.value = { 
        ...newVal,
        dueDate: newVal.dueDate ? newVal.dueDate.slice(0, 16) : '',
      };
      selectedFile.value = null;
    }
  },
  { immediate: true }
);

function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    selectedFile.value = input.files[0];
    console.log('File selected:', selectedFile.value.name);
    console.log('File details:', {
      name: selectedFile.value.name,
      type: selectedFile.value.type,
      size: selectedFile.value.size
    });
  } else {
    selectedFile.value = null;
    console.log('No file selected');
  }
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' bytes';
  else if (bytes < 1048576) return (bytes / 1024).toFixed(2) + ' KB';
  else return (bytes / 1048576).toFixed(2) + ' MB';
}

function close() {
  dialog.value = false;
}

async function save() {
  console.log('Save button clicked');

  // Validate the form
  if (form.value) {
    const { valid: isValid } = await form.value.validate();
    if (!isValid) {
      console.error('Form validation failed');
      return;
    }
  }

  // Create FormData
  const formData = new FormData();

  // Add the selected file to FormData
  if (selectedFile.value) {
    console.log(`Appending file "${selectedFile.value.name}" to FormData`);
    formData.append('file', selectedFile.value);
  } else if (!props.isEdit) {
    console.error('Statement file is required for new projects');
    alert('Statement file is required');
    return;
  }

  // Add the project data to FormData
  const projectData = {
    ...editableProject.value,
    dueDate: editableProject.value.dueDate + ':00', // Ensure proper datetime format
  };
  console.log('Appending project data to FormData:', projectData);
  formData.append('project', new Blob([JSON.stringify(projectData)], { type: 'application/json' }));

  try {
    if (props.isEdit && editableProject.value.id) {
      console.log(`Updating project ${editableProject.value.id}`);
      await RemoteService.updateProject(editableProject.value.id, formData);
    } else {
      console.log(`Creating new project for unit ${unitId}`);
      await RemoteService.createProject(unitId, formData);
    }
    console.log('Project saved successfully');
    emit('save');
    close();
  } catch (error) {
    console.error('Failed to save project:', error);
  }
}
</script>

<style scoped>
.file-input {
  padding: 10px 0;
  width: 100%;
}
</style>