<template>
  <v-container fluid class="pa-6">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-6">
          <v-btn 
            icon="mdi-arrow-left" 
            variant="text" 
            size="large"
            color="primary"
            @click="$router.go(-1)"
            class="mr-4"
          >
          </v-btn>
          <div>
            <h1 class="text-h4 font-weight-bold text-primary mb-1">Project Submissions</h1>
            <p v-if="project" class="text-h6 text-medium-emphasis mb-0">{{ project.title }}</p>
          </div>
        </div>
      </v-col>
    </v-row>

    <!-- Teacher View - Enhanced Cards -->
    <div v-if="roleStore.isTeacher && groups.length > 0">
      <v-row>
        <v-col v-for="group in groups" :key="group.id" cols="12" lg="6" xl="4">
          <v-card 
            elevation="2" 
            class="mb-6 group-card"
            :class="{ 'graded-card': group.grade !== null }"
          >
            <!-- Card Header with Group Info -->
            <v-card-title class="pa-4">
              <div class="d-flex justify-space-between align-center w-100">
                <div class="d-flex align-center">
                  <v-avatar 
                    :color="group.grade !== null ? 'success' : 'primary'" 
                    size="40" 
                    class="mr-3"
                    variant="tonal"
                  >
                    <v-icon icon="mdi-account-group"></v-icon>
                  </v-avatar>
                  <div>
                    <h3 class="text-h6 font-weight-bold mb-1">Group {{ group.groupId }}</h3>
                    <p class="text-body-2 text-medium-emphasis mb-0">
                      {{ group.members.length }} member{{ group.members.length !== 1 ? 's' : '' }}
                    </p>
                  </div>
                </div>
                <v-chip 
                  :color="group.grade !== null ? 'success' : 'surface-variant'" 
                  size="large"
                  class="font-weight-bold"
                  variant="tonal"
                >
                  {{ group.grade !== null ? `${group.grade}/20` : 'Not graded' }}
                </v-chip>
              </div>
            </v-card-title>
            
            <!-- Members Section -->
            <v-card-text class="pa-4 pt-0">
              <div class="d-flex align-center mb-2">
                <v-icon icon="mdi-account-multiple" size="small" class="mr-2" color="primary"></v-icon>
                <span class="font-weight-medium text-on-surface">Members:</span>
              </div>
              <div class="d-flex flex-wrap gap-2 mb-4">
                <v-chip 
                  v-for="member in group.members" 
                  :key="member.id"
                  size="small"
                  color="surface-variant"
                  variant="tonal"
                >
                  {{ member.name }}
                </v-chip>
              </div>
              
              <v-divider class="mb-4"></v-divider>
              
              <!-- Submission Info -->
              <div v-if="group.submissions.length > 0" class="submission-section">
                <div class="d-flex align-center mb-3">
                  <v-icon icon="mdi-file-upload" color="success" size="small" class="mr-2"></v-icon>
                  <span class="font-weight-medium text-success">Latest Submission</span>
                </div>
                
                <v-card variant="tonal" color="success" class="submission-details pa-3 mb-4">
                  <div class="d-flex justify-space-between align-center">
                    <div class="flex-grow-1">
                      <p class="text-body-2 font-weight-medium mb-1">
                        {{ group.submissions[0].submissionFile?.fileName || 'Unknown file' }}
                      </p>
                      <p class="text-caption text-medium-emphasis mb-1">
                        Submitted {{ formatDate(group.submissions[0].submissionDate) }}
                      </p>
                      <p class="text-caption text-medium-emphasis mb-0">
                        Avaliação Automática: {{ group.submissions[0].grade !== null ? group.submissions[0].grade.toFixed(1) : 'Not graded' }}
                      </p>
                      <p class="text-caption text-medium-emphasis mb-0">
                        by <span class="font-weight-medium">{{ group.submissions[0].submittedBy }}</span>
                      </p>
                    </div>
                    <v-btn 
                      icon="mdi-download"
                      variant="text"
                      color="success"
                      size="small"
                      @click="downloadSubmission(group.submissions[0].id)"
                    ></v-btn>
                  </div>
                </v-card>
              </div>
              
              <div v-else class="no-submission-section mb-4">
                <v-alert type="info" variant="tonal" density="compact">
                  <div class="d-flex align-center">
                    <v-icon icon="mdi-file-question" class="mr-2"></v-icon>
                    <span>No submissions yet</span>
                  </div>
                </v-alert>
              </div>
              
              <!-- Graded Teacher Info -->
              <div v-if="group.gradedTeacherName">
                <v-divider class="mb-3"></v-divider>
                <div class="d-flex align-center">
                  <v-icon icon="mdi-account-check" size="small" color="success" class="mr-2"></v-icon>
                  <span class="text-body-2">
                    <span class="font-weight-medium">Graded by:</span> {{ group.gradedTeacherName }}
                  </span>
                </div>
              </div>
            </v-card-text>
            
            <!-- Grading Section -->
            <v-card-actions class="pa-4 pt-0">
              <div class="w-100">
                <v-divider class="mb-4"></v-divider>
                <v-card variant="tonal" color="surface-variant" class="pa-3">
                  <div class="d-flex align-center mb-3">
                    <v-icon icon="mdi-star" color="warning" size="small" class="mr-2"></v-icon>
                    <span class="font-weight-medium">Atribuir Nota</span>
                    
                  </div>
                  
                  <div class="d-flex align-center gap-2">
                    <v-text-field
                      v-model.number="grades[group.id]"
                      label="Grade (0-20)"
                      type="number"
                      min="0"
                      max="20"
                      step="0.5"
                      density="compact"
                      variant="outlined"
                      hide-details
                      class="grade-input"
                      :error="isInvalidGrade(grades[group.id])"
                    >
                      <template v-slot:append-inner>
                        <span class="text-medium-emphasis">/20</span>
                      </template>
                    </v-text-field>
                    
                    <v-btn 
                      color="primary" 
                      variant="elevated"
                      @click="gradeGroup(group.id)"
                      :disabled="isInvalidGrade(grades[group.id])"
                      :loading="gradingLoading[group.id]"
                      class="grade-btn"
                    >
                      <v-icon icon="mdi-check" size="small" class="mr-1"></v-icon>
                      Grade
                    </v-btn>
                  </div>
                </v-card>
              </div>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </div>

    <!-- Student View - Enhanced -->
    <div v-else-if="roleStore.isStudent && myGroup">
      <v-row>
        <v-col cols="12">
          <v-card elevation="2" class="student-card">
            <v-card-title class="pa-6 bg-primary">
              <div class="d-flex justify-space-between align-center w-100">
                <div class="d-flex align-center">
                  <v-avatar color="black" size="48" class="mr-4">
                    <v-icon icon="mdi-account-group" color="primary" size="24"></v-icon>
                  </v-avatar>
                  <div>
                    <h2 class="text-h5 font-weight-bold mb-1">My Group</h2>
                    <p class="text-body-1 text-on-primary mb-0">Group {{ myGroup.groupId }}</p>
                  </div>
                </div>
                <v-chip 
                  :color="myGroup.grade !== null ? 'success' : 'surface-variant'" 
                  size="large"
                  variant="elevated"
                  class="font-weight-bold"
                >
                  <v-icon 
                    :icon="myGroup.grade !== null ? 'mdi-star' : 'mdi-clock-outline'" 
                    size="small"
                    class="mr-2"
                  ></v-icon>
                  {{ myGroup.grade !== null ? `${myGroup.grade}/20` : 'Not graded' }}
                </v-chip>
              </div>
            </v-card-title>
            
            <!-- Members Section -->
            <v-card-text class="pa-6">
              <div class="members-section mb-6">
                <div class="d-flex align-center mb-4">
                  <v-icon icon="mdi-account-multiple" color="primary" size="20" class="mr-3"></v-icon>
                  <h3 class="text-h6 font-weight-bold">Team Members</h3>
                </div>
                <div class="d-flex flex-wrap gap-2">
                  <v-chip 
                    v-for="member in myGroup.members" 
                    :key="member.id"
                    size="default"
                    color="primary"
                    variant="tonal"
                    class="font-weight-medium"
                  >
                    <v-icon icon="mdi-account" size="small" class="mr-2"></v-icon>
                    {{ member.name }}
                  </v-chip>
                </div>
              </div>
              
              <!-- Submission History -->
              <div class="submissions-section">
                <div class="d-flex align-center mb-4">
                  <v-icon icon="mdi-history" color="primary" size="20" class="mr-3"></v-icon>
                  <h3 class="text-h6 font-weight-bold">Submission History</h3>
                </div>
                
                <div v-if="myGroup.submissions.length > 0" class="submission-list">
                  <v-card
                    v-for="(submission, index) in myGroup.submissions"
                    :key="submission.id"
                    :variant="index === 0 ? 'tonal' : 'outlined'"
                    :color="index === 0 ? 'success' : 'surface-variant'"
                    class="mb-3 pa-4"
                  >
                    <div class="d-flex align-center">
                      <v-avatar 
                        :color="index === 0 ? 'success' : 'surface-variant'" 
                        size="40"
                        class="mr-4"
                        variant="tonal"
                      >
                        <v-icon 
                          :icon="index === 0 ? 'mdi-file-star' : 'mdi-file'" 
                          size="20"
                        ></v-icon>
                      </v-avatar>
                      
                      <div class="flex-grow-1">
                        <div class="d-flex align-center mb-1">
                          <span class="text-body-1 font-weight-bold mr-2">
                            {{ submission.submissionFile?.fileName || 'Unknown file' }}
                          </span>
                          <v-chip 
                            v-if="index === 0" 
                            size="x-small" 
                            color="success" 
                            variant="elevated"
                          >
                            Latest
                          </v-chip>
                        </div>
                        
                        <p class="text-body-2 mb-1">
                          <span class="font-weight-medium">Submitted by:</span> {{ submission.submittedBy }}
                        </p>
                        
                        <p class="text-caption text-medium-emphasis mb-0">
                          {{ formatDate(submission.submissionDate) }}
                        </p>
                        <p class="text-caption text-medium-emphasis mb-0">
                          Avaliação Automática: {{ submission.grade !== null ? submission.grade.toFixed(1) : 'Not graded' }}
                        </p>
                      </div>
                      
                      <v-btn 
                        icon="mdi-download" 
                        variant="text" 
                        :color="index === 0 ? 'success' : 'primary'"
                        @click="downloadSubmission(submission.id)"
                      ></v-btn>
                    </div>
                  </v-card>
                </div>
                
                <v-alert v-else type="info" variant="tonal">
                  <div class="d-flex align-center">
                    <v-icon icon="mdi-information" class="mr-3"></v-icon>
                    <div>
                      <p class="font-weight-bold mb-1">No submissions yet</p>
                      <p class="text-body-2 mb-0">Start by uploading your first project submission below.</p>
                    </div>
                  </div>
                </v-alert>
              </div>
            </v-card-text>
            
            <!-- Submission Form -->
            <v-card-actions v-if="!isPastDueDate" class="pa-6 pt-0">
              <div class="w-100 upload-section">
                <v-divider class="mb-6"></v-divider>
                
                <div class="d-flex align-center mb-4">
                  <v-icon icon="mdi-upload" color="primary" size="20" class="mr-3"></v-icon>
                  <h3 class="text-h6 font-weight-bold">New Submission</h3>
                </div>
                
                <div class="upload-area">
                  <v-card
                    variant="outlined"
                    class="file-upload-card pa-6 text-center"
                    :class="{ 'file-selected': selectedFile }"
                    @click="triggerFileInput"
                  >
                    <v-icon 
                      icon="mdi-cloud-upload" 
                      size="48" 
                      color="primary" 
                      class="mb-3"
                    ></v-icon>
                    <p class="text-h6 font-weight-bold mb-2">
                      {{ selectedFile ? 'File Selected' : 'Choose File to Upload' }}
                    </p>
                    <p class="text-body-2 text-medium-emphasis mb-0">
                      {{ selectedFile ? selectedFile.name : 'Click here to select your project file' }}
                    </p>
                  </v-card>
                  
                  <input
                    ref="fileInput"
                    type="file"
                    @change="handleFileChange"
                    style="display: none"
                  />
                  
                  <div v-if="selectedFile" class="file-info mt-4 text-center">
                    <v-chip color="success" size="large" variant="tonal">
                      <v-icon icon="mdi-check-circle" size="small" class="mr-2"></v-icon>
                      {{ selectedFile.name }} ({{ formatFileSize(selectedFile.size) }})
                    </v-chip>
                  </div>
                  
                  <v-btn 
                    color="primary" 
                    size="large"
                    variant="elevated"
                    @click="submitFile" 
                    :disabled="!selectedFile"
                    :loading="submitting"
                    class="mt-6 w-100"
                    height="56"
                  >
                    <v-icon icon="mdi-upload" class="mr-2"></v-icon>
                    <span class="font-weight-bold">Submit Project</span>
                  </v-btn>
                </div>
              </div>
            </v-card-actions>
            
            <v-card-text v-else class="pa-6">
              <v-alert type="warning" variant="tonal">
                <div class="d-flex align-center">
                  <v-icon icon="mdi-clock-alert" class="mr-3"></v-icon>
                  <div>
                    <p class="font-weight-bold mb-1">Submission Deadline Passed</p>
                    <p class="text-body-2 mb-0">The deadline for submissions has expired.</p>
                  </div>
                </div>
              </v-alert>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
    
    <!-- Loading/Error States -->
    <div v-else-if="loading" class="text-center py-12">
      <v-progress-circular 
        indeterminate 
        size="64" 
        color="primary"
        class="mb-4"
      ></v-progress-circular>
      <p class="text-h6 font-weight-medium text-medium-emphasis">Loading group information...</p>
    </div>
    
    <div v-else class="text-center py-12">
      <v-alert type="info" variant="tonal" max-width="500" class="mx-auto">
        <div class="text-center">
          <v-icon icon="mdi-information" size="48" class="mb-3"></v-icon>
          <p class="text-h6 font-weight-bold mb-2">
            {{ roleStore.isStudent ? 'No Group Assignment' : 'No Groups Found' }}
          </p>
          <p class="text-body-2 mb-0">
            {{ roleStore.isStudent ? 'You have not been assigned to a group for this project.' : 'No groups have been created for this project yet.' }}
          </p>
        </div>
      </v-alert>
    </div>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleStore } from '@/stores/role'
import RemoteService from '@/services/RemoteService'
import type StudentGroupDto from '@/models/StudentGroupDto'
import type ProjectDto from '@/models/ProjectDto'

const route = useRoute()
const roleStore = useRoleStore()

const projectId = Number(route.params.projectId)
const unitId = Number(route.params.id)

const project = ref<ProjectDto | null>(null)
const groups = ref<StudentGroupDto[]>([])
const grades = ref<Record<number, number | null>>({})
const gradingLoading = ref<Record<number, boolean>>({})
const myGroup = ref<StudentGroupDto | null>(null)
const selectedFile = ref<File | null>(null)
const loading = ref(true)
const submitting = ref(false)
const fileInput = ref<HTMLInputElement>()

const isPastDueDate = computed(() => {
  if (!project.value?.dueDate) return false
  return new Date(project.value.dueDate) < new Date()
})

onMounted(async () => {
  try {
    loading.value = true
    
    const allProjects = await RemoteService.getProjects(unitId)
    project.value = allProjects.find(p => p.id === projectId) || null

    if (roleStore.isTeacher) {
      groups.value = await RemoteService.getProjectGroups(projectId)
      groups.value.forEach(g => {
        if (g.id != null) {
          grades.value[g.id] = g.grade
          gradingLoading.value[g.id] = false
        }
      })
    } else if (roleStore.isStudent && roleStore.getCurrentUserID) {
      myGroup.value = await RemoteService.getMyProjectGroup(projectId, roleStore.getCurrentUserID)
    }
  } catch (error) {
    console.error("Failed to load submission data:", error)
  } finally {
    loading.value = false
  }
})

function triggerFileInput() {
  fileInput.value?.click()
}

function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    selectedFile.value = input.files[0]
  } else {
    selectedFile.value = null
  }
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' bytes'
  else if (bytes < 1048576) return (bytes / 1024).toFixed(2) + ' KB'
  else return (bytes / 1048576).toFixed(2) + ' MB'
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function isInvalidGrade(grade: number | null): boolean {
  return grade === null || grade < 0 || grade > 20
}

async function gradeGroup(groupId: number) {
  const grade = grades.value[groupId]
  const teacherId = roleStore.getCurrentUserID

  if (isInvalidGrade(grade) || !teacherId) {
    return
  }

  try {
    gradingLoading.value[groupId] = true
    await RemoteService.gradeGroup(groupId, teacherId, grade!)
    
    // Refresh data
    groups.value = await RemoteService.getProjectGroups(projectId)
    groups.value.forEach(g => {
      if (g.id != null) grades.value[g.id] = g.grade
    })
  } catch (error) {
    console.error("Failed to save grade:", error)
  } finally {
    gradingLoading.value[groupId] = false
  }
}

async function submitFile() {
  const studentId = roleStore.getCurrentUserID
  if (!myGroup.value || !selectedFile.value || !studentId) return

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    submitting.value = true
    await RemoteService.submitToGroup(myGroup.value.id, studentId, formData)
    
    // Refresh data and reset form
    myGroup.value = await RemoteService.getMyProjectGroup(projectId, studentId)
    selectedFile.value = null
    
    // Reset file input
    if (fileInput.value) fileInput.value.value = ''
    
  } catch (error) {
    console.error("Failed to submit file:", error)
  } finally {
    submitting.value = false
  }
}

function downloadSubmission(submissionId: number | null) {
  if (submissionId) {
    const url = RemoteService.getSubmissionFileUrl(submissionId)
    window.open(url, '_blank')
  }
}
</script>

<style scoped>
.v-container {
  background: rgba(var(--v-theme-surface), 0.8);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.group-card {
  transition: all 0.3s ease;
  border-left: 3px solid rgb(var(--v-theme-primary));
}

.group-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.graded-card {
  border-left-color: rgb(var(--v-theme-success));
}

.grade-input {
  max-width: 120px;
}

.grade-btn {
  min-width: 100px;
}

.student-card {
  background: rgb(var(--v-theme-surface));
}

.file-upload-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px dashed rgba(var(--v-theme-primary), 0.5);
}

.file-upload-card:hover {
  border-color: rgb(var(--v-theme-primary));
  background: rgba(var(--v-theme-primary), 0.05);
  transform: translateY(-2px);
}

.file-selected {
  border-color: rgb(var(--v-theme-success));
  background: rgba(var(--v-theme-success), 0.05);
}

.submission-details {
  border-left: 3px solid rgb(var(--v-theme-success));
}
</style>