import axios from 'axios'
import type ProjectDto from '@/models/ProjectDto'
import { useAppearanceStore } from '@/stores/appearance'
import DeiError from '@/models/DeiError'
import type PersonDto from '@/models/PersonDto'
import type CourseDto from '@/models/CourseDto'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import type EnrollmentDto from '@/models/EnrollmentDto'
import type AssistDto from '@/models/AssistDto'
import type StudentGroupDto from '@/models/StudentGroupDto'
import type MaterialDto from '@/models/MaterialDto'
import type { CreateMaterialDto } from '@/models/MaterialDto'
import type TesteDto from '@/models/TesteDto'
import type { CreateTesteDto } from '@/models/TesteDto'
import type StudentTesteDto from '@/models/StudentTesteDto'
import type RevisionDto from '@/models/RevisionDto'
import type FinalGradeBreakdownDto from '@/models/FinalGradeBreakdownDto'
import type { UnitFinalGradeDto, AssessmentDeliveryDto, ProgressDto } from '@/models/StudentViewsDto'
import type { TeacherStudentDto, PendingGradingDto, TeacherStatisticsDto } from '@/models/TeacherViewsDto'
import type { AssistantGradingTaskDto, AssistantStatisticsDto } from '@/models/AssistantViewsDto'

const httpClient = axios.create()
httpClient.defaults.timeout = 50000
httpClient.defaults.baseURL = import.meta.env.VITE_ROOT_API
httpClient.defaults.headers.post['Content-Type'] = 'application/json'

export default class RemoteServices {

  // ! PEOPLE

  static async getPeople(): Promise<PersonDto[]> {
    return httpClient.get('/people')
  }


  static async createPerson(person: PersonDto): Promise<PersonDto> {
    return httpClient.post('/people', person)
  }

  static async deletePerson(id: number): Promise<void> {
    return httpClient.delete(`/people/${id}`)
  }

  static async updatePerson(id: number, person: PersonDto): Promise<PersonDto> {
  return httpClient.put(`/people/${id}`, person)
  }

    static async getMainTeachers(): Promise<PersonDto[]> {

    return httpClient.get('/people/main-teachers')
  }

  // ! COURSES

  static async getCourses(): Promise<CourseDto[]> {
    return httpClient.get('/courses')
  }

  static async updateCourse(id: number, course: CourseDto): Promise<CourseDto> {
    return httpClient.put(`/courses/${id}`, course)
  }

  static async createCourse(course: CourseDto): Promise<CourseDto> {
    return httpClient.post('/courses', course)
  }

  static async deleteCourse(id: number): Promise<void> {
    return httpClient.delete(`/courses/${id}`)
  
  }

  // ! CURRICULUM UNITS

  static async getCurriculumUnits(): Promise<CurriculumUnitDto[]> {
    return httpClient.get('/curriculum-units')
  }

  static async createCurriculumUnit(unit: CurriculumUnitDto): Promise<CurriculumUnitDto> {
    return httpClient.post('/curriculum-units', unit)
  }

  static async updateCurriculumUnit(id: number, unit: CurriculumUnitDto): Promise<CurriculumUnitDto> {
  return httpClient.put(`/curriculum-units/${id}`, unit)
  }
  
  static async getCurriculumUnit(id: number): Promise<CurriculumUnitDto> {
    return httpClient.get(`/curriculum-units/${id}`)
  }

  static async deleteCurriculumUnit(id: number): Promise<void> {
    return httpClient.delete(`/curriculum-units/${id}`)
   
  }

  static async getAssistants(unitId: number): Promise<AssistDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/assistants`)
  }


  static async addAssistant(unitId: number, personId: number): Promise<AssistDto> {
    return httpClient.post(`/curriculum-units/${unitId}/assistants`, { id: personId })
  }

  static async removeAssistant(unitId: number, personId: number): Promise<void> {
    return httpClient.delete(`/curriculum-units/${unitId}/assistants/${personId}`)
  }

  static async getStudents(unitId: number): Promise<EnrollmentDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/students`)
  }

  static async addStudent(unitId: number, personId: number): Promise<EnrollmentDto> {
    return httpClient.post(`/curriculum-units/${unitId}/students`, { id: personId })
  }

  static async removeStudent(unitId: number, personId: number): Promise<void> {
    return httpClient.delete(`/curriculum-units/${unitId}/students/${personId}`)
  }

  static async getCurriculumUnitsByPerson(personId: number): Promise<CurriculumUnitDto[]> {
    return httpClient.get(`/curriculum-units/person/${personId}`)
  }

  static async getFinalGradeBreakdown(studentId: number, curriculumUnitId: number): Promise<FinalGradeBreakdownDto> {
    return httpClient.get(
        `/final-grade-breakdown?studentId=${studentId}&curriculumUnitId=${curriculumUnitId}`
        )
  }

  static async getStudentProfile(studentId: number): Promise<UnitFinalGradeDto[]> {
    return httpClient.get(`student/${studentId}/profile`)
  }

  /**
   * Get student assessment deliveries for calendar view
   */
  static async getStudentAssessmentDeliveries(studentId: number): Promise<AssessmentDeliveryDto[]> {
    return httpClient.get(`student/${studentId}/deliveries`)
  }
  // ! STUDENT
  /**
   * Get student progress statistics
   */
  static async getStudentProgress(studentId: number): Promise<ProgressDto> {
    return httpClient.get(`student/${studentId}/progress`)
  }

  // ! TEACHERS

  static async getTeacherStudents(teacherId: number): Promise<TeacherStudentDto[]> {
    return httpClient.get(`/teacher/${teacherId}/students`)
  }

  /**
   * Get assessments with pending grading tasks for the teacher
   */
  static async getTeacherPendingGrading(teacherId: number): Promise<PendingGradingDto[]> {
    return httpClient.get(`/teacher/${teacherId}/pending-grading`)
  }

  /**
   * Get teacher statistics and performance metrics
   * Only available for main teachers
   */
  static async getTeacherStatistics(teacherId: number): Promise<TeacherStatisticsDto> {
    return httpClient.get(`/teacher/${teacherId}/statistics`)
  }


  // ! assistants
  static async getAssistantGradingTasks(assistantId: number): Promise<AssistantGradingTaskDto[]> {
    return httpClient.get(`/assistant/${assistantId}/grading-tasks`)
  }

  /**
   * Get assistant statistics and performance metrics
   */
  static async getAssistantStatistics(assistantId: number): Promise<AssistantStatisticsDto> {
    return httpClient.get(`/assistant/${assistantId}/statistics`)
  }

  // ! PROJECTS
    static async getProjects(unitId: number): Promise<ProjectDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/projects`);
  }

  static async createProject(unitId: number, formData: FormData): Promise<ProjectDto> {
    
    
    return httpClient.post(`/curriculum-units/${unitId}/projects`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  static async updateProject(projectId: number, formData: FormData): Promise<ProjectDto> {

    
  
    return httpClient.put(`/projects/${projectId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  static getProjectStatementUrl(projectId: number): string {
    return `${import.meta.env.VITE_ROOT_API}/projects/${projectId}/statement`;
  }

  static getSubmissionFileUrl(fileId: number): string {
    
    return `${import.meta.env.VITE_ROOT_API}/projects/submissions/${fileId}`;
  }

  static getFileUrl(fileId: number): string {
    return `${import.meta.env.VITE_ROOT_API}/files/${fileId}`
  }


  static async getProjectGroups(projectId: number): Promise<StudentGroupDto[]> {
    return httpClient.get(`/projects/${projectId}`);
  }

  static async getMyProjectGroup(projectId: number, studentId: number): Promise<StudentGroupDto> {
    
    return httpClient.get(`/projects/${projectId}/${studentId}`);
  }


  // ! MATERIALS
  static async getMaterials(unitId: number): Promise<MaterialDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/materials`)
  }

  static async createMaterial(material: CreateMaterialDto): Promise<MaterialDto> {
    return httpClient.post('/materials', material)
  }

  static async addFileToMaterial(materialId: number, file: File): Promise<MaterialDto> {
    const formData = new FormData()
    formData.append('file', file)
    
    return httpClient.post(`/materials/${materialId}/files`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }

  static async deleteMaterial(materialId: number): Promise<void> {
    return httpClient.delete(`/materials/${materialId}`)
  }

  static async deleteFile(fileId: number): Promise<void> {
    return httpClient.delete(`/files/${fileId}`)
  }

  static async submitToGroup(groupId: number, studentId: number, formData: FormData): Promise<void> {

    // Backend expects groupId and studentId as request parameters, not path variables.
    return httpClient.post(`/groups/submit`, formData, {
      params: {
        groupId,
        studentId,
      },
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  }

  static async gradeGroup(groupId: number, teacherId: number, grade: number): Promise<void> {
  
    // Backend expects a GradingDto object in the request body.
    return httpClient.put(`/groups/grade`, { groupId, teacherId, grade });
  }


  // ! TESTS
  static async getTests(unitId: number): Promise<TesteDto[]> {
  return httpClient.get(`/curriculum-units/${unitId}/tests`)
  }

  static async createTest(unitId: number, test: CreateTesteDto): Promise<TesteDto> {
    return httpClient.post(`/curriculum-units/${unitId}/tests`, test)
  }

  static async addStatementFile(testId: number, file: File): Promise<TesteDto> {
    const formData = new FormData()
    formData.append('file', file)
    return httpClient.post(`/tests/${testId}/statement`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }

  static async addCorrectionFile(testId: number, file: File): Promise<TesteDto> {
    const formData = new FormData()
    formData.append('file', file)
    return httpClient.post(`/tests/${testId}/correction`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }

  static async getStudentTests(testId: number): Promise<StudentTesteDto[]> {
    return httpClient.get(`/tests/${testId}/student-tests`)
  }

  static async getStudentTest(testId: number, studentId: number): Promise<StudentTesteDto> {
    return httpClient.get(`/tests/${testId}/students/${studentId}`)
  }

  static async getStudentTestsForStudent(unitId: number, studentId: number): Promise<StudentTesteDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/students/${studentId}/tests`)
  }

  static async gradeStudentTest(studentTestId: number, teacherId: number, grade: number): Promise<void> {
    return httpClient.post(`/student-tests/${studentTestId}/grade`, { teacherId, grade })
  }

  // !Revision Management
  static async requestTestRevision(studentTesteId: number, justification: string): Promise<RevisionDto> {
  return httpClient.post('/revisions', {
    studentTesteId,
    justification
  })
  }

  static async getPendingRevisions(unitId: number): Promise<RevisionDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/revisions/pending`)
  }

  static async getAllRevisions(unitId: number): Promise<RevisionDto[]> {
    return httpClient.get(`/curriculum-units/${unitId}/revisions`)
  }

  static async gradeRevision(revisionId: number, teachingAssistantId: number, newGrade: number): Promise<RevisionDto> {
    return httpClient.post(`/revisions/${revisionId}/grade`, {
      teachingAssistantId,
      newGrade
    })
  }

  static async processRevision(revisionId: number, action: string, mainTeacherId: number): Promise<RevisionDto> {
    return httpClient.post(`/revisions/${revisionId}/process`, {
      action,
      mainTeacherId
    })
  }

  static async getTestRevisions(studentTestId: number): Promise<RevisionDto> {
    return httpClient.get(`/student-tests/${studentTestId}/revisions`)
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to the server'
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond'
    } else {
      return error.response?.data?.message ?? 'Unknown Error'
    }
  }



  static async handleError(error: any): Promise<never> {
    const deiErr = new DeiError(
      await RemoteServices.errorMessage(error),
      error.response?.data?.code ?? -1
    )
    const appearance = useAppearanceStore()
    appearance.pushError(deiErr)
    appearance.loading = false
    throw deiErr
  }
}

httpClient.interceptors.request.use((request) => request, RemoteServices.handleError)
httpClient.interceptors.response.use((response) => response.data, RemoteServices.handleError)
