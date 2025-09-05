<template>
  <div style="max-width: 600px; margin: 2rem auto;">
    <input type="file" @change="onFileChange" />
    <div v-if="file" style="margin-top: 1.5rem; border: 1px solid #ccc; border-radius: 8px; padding: 1rem;">
      <div><strong>Name:</strong> {{ file.name }}</div>
      <div><strong>Type:</strong> {{ file.type || 'Unknown' }}</div>
      <div style="margin-top: 1rem;">
        <strong>Content:</strong>
        <div v-if="fileContent !== null">
          <pre style="white-space: pre-wrap; word-break: break-all;">{{ fileContent }}</pre>
        </div>
        <div v-else>
          <em>Cannot display content (possibly a binary file).</em>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const file = ref<File | null>(null);
const fileContent = ref<string | null>(null);

function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement;
  if (!target.files || target.files.length === 0) {
    file.value = null;
    fileContent.value = null;
    return;
  }
  const selectedFile = target.files[0];
  file.value = selectedFile;

  const reader = new FileReader();
  reader.onload = () => {
    fileContent.value = typeof reader.result === 'string' ? reader.result : null;
  };
  reader.onerror = () => {
    fileContent.value = null;
  };
  reader.readAsText(selectedFile);
}
</script>