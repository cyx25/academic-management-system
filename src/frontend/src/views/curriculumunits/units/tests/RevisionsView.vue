<template>
  <v-container fluid>
    <!-- Header -->
    <v-row class="mb-4">
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold text-primary mb-2">Pending Revisions</h1>
        <p class="text-body-1 text-medium-emphasis">
          Review student revision requests and provide new grades
        </p>
      </v-col>
    </v-row>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <p class="text-h6 mt-4">Loading revisions...</p>
    </div>

    <!-- Revisions List -->
    <v-row v-else-if="pendingRevisions.length > 0">
      <v-col v-for="revision in pendingRevisions" :key="revision.id" cols="12">
        <RevisionCard 
          :revision="revision"
          @revision-graded="refreshRevisions"
        />
      </v-col>
    </v-row>

    <!-- Empty State -->
    <v-row v-else>
      <v-col cols="12">
        <v-alert type="info" variant="tonal" class="text-center py-8">
          <v-icon icon="mdi-comment-check" size="48" class="mb-4"></v-icon>
          <h3 class="text-h5 font-weight-bold mb-2">No Pending Revisions</h3>
          <p class="text-body-1">
            All revision requests have been processed.
          </p>
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import RemoteService from '@/services/RemoteService'
import RevisionCard from './RevisionCard.vue'
import type RevisionDto from '@/models/RevisionDto'

const route = useRoute()
const unitId = computed(() => Number(route.params.id))
const pendingRevisions = ref<RevisionDto[]>([])
const loading = ref(true)

onMounted(async () => {
  await refreshRevisions()
})

async function refreshRevisions() {
  try {
    loading.value = true
    pendingRevisions.value = await RemoteService.getPendingRevisions(unitId.value)
  } catch (error) {
    console.error('Failed to fetch pending revisions:', error)
  } finally {
    loading.value = false
  }
}
</script>