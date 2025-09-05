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
            <p v-if="project" class="text-h6 text-grey-darken-1 mb-0">{{ project.title }}</p>
          </div>
        </div>
      </v-col>
    </v-row>

    <!-- Teacher View - Enhanced Cards -->
    <div v-if="roleStore.isTeacher && groups.length > 0">
      <v-row>
        <v-col v-for="group in groups" :key="group.id" cols="12" lg="6" xl="4">
          <v-card 
            elevation="8" 
            class="mb-6 group-card"
            :class="{ 'graded-card': group.grade !== null }"
          >
            <!-- Card Header with Group Info -->
            <v-card-title class="pa-6 pb-4">
              <div class="d-flex justify-space-between align-center w-100">
                <div class="d-flex align-center">
                  <v-avatar 
                    :color="group.grade !== null ? 'success' : 'primary'" 
                    size="40" 
                    class="mr-3"
                  >
                    <v-icon icon="mdi-account-group" color="white"></v-icon>
                  </v-avatar>
                  <div>
                    <h3 class="text-h5 font-weight-bold mb-1">Group {{ group.groupId }}</h3>
                    <p class="text-body-2 text-grey-darken-1 mb-0">
                      {{ group.members.length }} member{{ group.members.length !== 1 ? 's' : '' }}
                    </p>
                  </div>
                </div>
                <v-chip 
                  :color="group.grade !== null ? 'success' : 'grey-lighten-1'" 
                  size="large"
                  class="font-weight-bold"
                  variant="flat"
                >
                  {{ group.grade !== null ? `${group.grade}/20` : 'Not graded' }}
                </v-chip>
              </div>
            </v-card-title>
            
            <!-- Members Section -->
            <v-card-subtitle class="px-6 pb-4">
              <div class="d-flex align-center mb-2">
                <v-icon icon="mdi-account-multiple" size="small" class="mr-2 text-primary"></v-icon>
                <span class="font-weight-bold text-grey-darken-2">Members:</span>
              </div>
              <div class="d-flex flex-wrap gap-2">
                <v-chip 
                  v-for="member in group.members" 
                  :key="member.id"
                  size="small"
                  color="blue-grey-lighten-4"
                  text-color="blue-grey-darken-3"
                >
                  {{ member.name }}
                </v-chip>
              </div>
            </v-card-subtitle>
            
            <!-- Submission Info -->
            <v-card-text class="px-6 pt-0">
              <v-divider class="mb-4"></v-divider>
              
              <div v-if="group.submissions.length > 0" class="submission-section">
                <div class="d-flex align-center mb-3">
                  <v-icon icon="mdi-file-upload" color="success" class="mr-2"></v-icon>
                  <span class="font-weight-bold text-success">Latest Submission</span>
                </div>
                
                <div class="submission-details bg-grey-lighten-5 pa-4 rounded">
                  <div class="d-flex justify-space-between align-center mb-2">
                    <div>
                      <p class="text-body-1 font-weight-medium mb-1">
                        {{ group.submissions[0].submissionFile?.fileName || 'Unknown file' }}
                      </p>
                      <p class="text-body-2 text-grey-darken-1 mb-1">
                        Submitted {{ formatDate(group.submissions[0].submissionDate) }}
                      </p>
                      <p class="text-body-2 text-grey-darken-1 mb-0">
                        by <span class="font-weight-medium">{{ group.submissions[0].submittedBy }}</span>
                      </p>
                    </div>
                    <v-btn 
                      icon="mdi-download"
                      variant="elevated"
                      color="primary"
                      size="large"
                      @click="downloadSubmission(group.submissions[0].id)"
                    ></v-btn>
                  </div>
                </div>
              </div>
              
              <div v-else class="no-submission-section">
                <div class="d-flex align-center justify-center py-6">
                  <div class="text-center">
                    <v-icon icon="mdi-file-question" size="48" color="grey-lighten-1" class="mb-3"></v-icon>
                    <p class="text-h6 text-grey-darken-1 mb-0">No submissions yet</p>
                  </div>
                </div>
              </div>
              
              <!-- Graded Teacher Info -->
              <div v-if="group.gradedTeacherName" class="mt-4">
                <v-divider class="mb-3"></v-divider>
                <div class="d-flex align-center">
                  <v-icon icon="mdi-account-check" size="small" color="success" class="mr-2"></v-icon>
                  <span class="text-body-2 text-success">
                    <span class="font-weight-bold">Graded by:</span> {{ group.gradedTeacherName }}
                  </span>
                </div>
              </div>
            </v-card-text>
            
            <!-- Grading Section -->
            <v-card-actions class="px-6 pb-6 pt-0">
              <div class="w-100">
                <v-divider class="mb-4"></v-divider>
                <div class="grading-section">
                  <div class="d-flex align-center mb-3">
                    <v-icon icon="mdi-star" color="amber" class="mr-2"></v-icon>
                    <span class="font-weight-bold text-grey-darken-2">Grade Assignment</span>
                  </div>
                  
                  <div class="d-flex align-center gap-3">
                    <v-text-field
                      v-model.number="grades[group.id]"
                      label="Grade (0-20)"
                      type="number"
                      min="0"
                      max="20"
                      step="0.5"
                      density="comfortable"
                      variant="outlined"
                      hide-details
                      class="grade-input"
                      :class="{ 'error-input': isInvalidGrade(grades[group.id]) }"
                    >
                      <template v-slot:append-inner>
                        <span class="text-grey-darken-1">/20</span>
                      </template>
                    </v-text-field>
                    
                    <v-btn 
                      color="primary" 
                      size="large"
                      variant="elevated"
                      @click="gradeGroup(group.id)"
                      :disabled="isInvalidGrade(grades[group.id])"
                      :loading="gradingLoading[group.id]"
                      class="grade-btn"
                    >
                      <v-icon icon="mdi-check" class="mr-2"></v-icon>
                      Grade
                    </v-btn>
                  </div>
                </div>
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
          <v-card elevation="12" class="student-card">
            <v-card-title class="pa-6 pb-4 bg-gradient-primary">
              <div class="d-flex justify-space-between align-center w-100">
                <div class="d-flex align-center">
                  <v-avatar color="white" size="48" class="mr-4">
                    <v-icon icon="mdi-account-group" color="primary" size="24"></v-icon>
                  </v-avatar>
                  <div>
                    <h2 class="text-h4 font-weight-bold text-white mb-1">My Group</h2>
                    <p class="text-h6 text-blue-lighten-3 mb-0">Group {{ myGroup.groupId }}</p>
                  </div>
                </div>
                <v-chip 
                  :color="myGroup.grade !== null ? 'success' : 'grey-lighten-2'" 
                  size="x-large"
                  variant="elevated"
                  class="font-weight-bold"
                >
                  <v-icon 
                    :icon="myGroup.grade !== null ? 'mdi-star' : 'mdi-clock-outline'" 
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
                  <v-icon icon="mdi-account-multiple" color="primary" size="24" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold text-primary">Team Members</h3>
                </div>
                <div class="d-flex flex-wrap gap-3">
                  <v-chip 
                    v-for="member in myGroup.members" 
                    :key="member.id"
                    size="large"
                    color="primary"
                    variant="tonal"
                    class="font-weight-medium"
                  >
                    <v-icon icon="mdi-account" class="mr-2"></v-icon>
                    {{ member.name }}
                  </v-chip>
                </div>
              </div>
              
              <!-- Submission History -->
              <div class="submissions-section">
                <div class="d-flex align-center mb-4">
                  <v-icon icon="mdi-history" color="primary" size="24" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold text-primary">Submission History</h3>
                </div>
                
                <v-list v-if="myGroup.submissions.length > 0" class="submission-list">
                  <v-list-item
                    v-for="(submission, index) in myGroup.submissions"
                    :key="submission.id"
                    class="submission-item mb-3"
                    :class="{ 'latest-submission': index === 0 }"
                  >
                    <template v-slot:prepend>
                      <v-avatar 
                        :color="index === 0 ? 'success' : 'grey-lighten-1'" 
                        size="56"
                        class="mr-4"
                      >
                        <v-icon 
                          :icon="index === 0 ? 'mdi-file-star' : 'mdi-file'" 
                          color="white"
                          size="24"
                        ></v-icon>
                      </v-avatar>
                    </template>
                    
                    <div class="submission-content flex-grow-1">
                      <v-list-item-title class="text-h6 font-weight-bold mb-1">
                        {{ submission.submissionFile?.fileName || 'Unknown file' }}
                        <v-chip 
                          v-if="index === 0" 
                          size="small" 
                          color="success" 
                          class="ml-2 font-weight-bold"
                        >
                          Latest
                        </v-chip>
                      </v-list-item-title>
                      
                      <v-list-item-subtitle class="text-body-1 mb-2">
                        <span class="font-weight-bold">Submitted by:</span> {{ submission.submittedBy }}
                      </v-list-item-subtitle>
                      
                      <v-list-item-subtitle class="text-body-2 text-grey-darken-1">
                        {{ formatDate(submission.submissionDate) }}
                      </v-list-item-subtitle>
                    </div>
                    
                    <template v-slot:append>
                      <v-btn 
                        icon="mdi-download" 
                        variant="elevated" 
                        color="primary"
                        size="large"
                        @click="downloadSubmission(submission.id)"
                      ></v-btn>
                    </template>
                  </v-list-item>
                </v-list>
                
                <v-alert v-else type="info" variant="tonal" class="ma-0">
                  <div class="d-flex align-center">
                    <v-icon icon="mdi-information" class="mr-3"></v-icon>
                    <div>
                      <p class="text-h6 font-weight-bold mb-1">No submissions yet</p>
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
                  <v-icon icon="mdi-upload" color="primary" size="24" class="mr-3"></v-icon>
                  <h3 class="text-h5 font-weight-bold text-primary">New Submission</h3>
                </div>
                
                <div class="upload-area">
                  <label for="submission-file" class="file-upload-label">
                    <div class="upload-content">
                      <v-icon icon="mdi-cloud-upload" size="48" color="primary" class="mb-3"></v-icon>
                      <p class="text-h6 font-weight-bold text-primary mb-2">
                        {{ selectedFile ? 'File Selected' : 'Choose File to Upload' }}
                      </p>
                      <p class="text-body-2 text-grey-darken-1 mb-0">
                        {{ selectedFile ? selectedFile.name : 'Click here or drag and drop your project file' }}
                      </p>
                    </div>
                  </label>
                  
                  <input
                    id="submission-file"
                    type="file"
                    @change="handleFileChange"
                    class="file-input-hidden"
                  />
                  
                  <div v-if="selectedFile" class="file-info mt-4">
                    <v-chip color="success" size="large" class="font-weight-medium">
                      <v-icon icon="mdi-check-circle" class="mr-2"></v-icon>
                      {{ selectedFile.name }} ({{ formatFileSize(selectedFile.size) }})
                    </v-chip>
                  </div>
                  
                  <v-btn 
                    color="primary" 
                    size="x-large"
                    variant="elevated"
                    @click="submitFile" 
                    :disabled="!selectedFile"
                    :loading="submitting"
                    class="mt-6 submit-btn"
                    block
                  >
                    <v-icon icon="mdi-upload" class="mr-3"></v-icon>
                    <span class="font-weight-bold">Submit Project</span>
                  </v-btn>
                </div>
              </div>
            </v-card-actions>
            
            <v-card-text v-else class="pa-6">
              <v-alert type="warning" variant="tonal" class="ma-0">
                <div class="d-flex align-center">
                  <v-icon icon="mdi-clock-alert" class="mr-3"></v-icon>
                  <div>
                    <p class="text-h6 font-weight-bold mb-1">Submission Deadline Passed</p>
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
      <p class="text-h5 font-weight-medium text-grey-darken-1">Loading group information...</p>
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
    const input = document.getElementById('submission-file') as HTMLInputElement
    if (input) input.value = ''
    
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
.group-card {
  transition: all 0.3s ease;
  border-left: 4px solid #1976d2;
}

.group-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 20px rgba(0,0,0,0.15) !important;
}

.graded-card {
  border-left-color: #4caf50;
}

.grade-input {
  max-width: 140px;
}

.grade-btn {
  min-width: 120px;
}

.error-input .v-field {
  border-color: #f44336;
}

.student-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
}

.submission-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.latest-submission {
  background: linear-gradient(135deg, #e8f5e8 0%, #f1f8e9 100%);
  border-color: #4caf50;
}

.file-upload-label {
  display: block;
  width: 100%;
  padding: 32px;
  border: 2px dashed #1976d2;
  border-radius: 12px;
  background: #f8f9ff;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.file-upload-label:hover {
  background: #f0f4ff;
  border-color: #1565c0;
  transform: translateY(-2px);
}

.file-input-hidden {
  display: none;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.submit-btn {
  height: 56px;
  font-size: 1.1rem;
}

.grading-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.submission-details {
  border-left: 4px solid #4caf50;
}
</style>