import axios from 'axios'
import type { AxiosResponse } from 'axios'
import { useAppearanceStore } from '@/stores/appearance'
import DeiError from '@/models/DeiError'
import type PersonDto from '@/models/PersonDto'
import type CourseDto from '@/models/CourseDto'
import type CurriculumUnitDto from '@/models/CurriculumUnitDto'
import type EnrollmentDto from '@/models/EnrollmentDto'
import type AssistDto from '@/models/AssistDto'

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
