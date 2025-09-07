import CourseDto from './CourseDto'
import type PersonDto from './PersonDto'

export interface CurriculumUnitDto {
  id?: number
  name?: string
  code?: string
  semester?: number
  ects?: number
  mainTeacher?: PersonDto
  courses?: CourseDto[]


}


export interface AssessmentAverageDto {
  assessmentType: 'TEST' | 'PROJECT'
  assessmentTitle: string
  averageGrade: number | null
  totalGraded: number
  totalStudents: number
}


export interface RevisionStatisticsDto {
  totalSubmitted: number
  totalApproved: number
  totalDenied: number
  totalPending: number
}


export interface GradeDistributionDto {
  excellent: number     // 18-20
  veryGood: number      // 16-17
  good: number          // 14-15
  satisfactory: number  // 10-13
  insufficient: number  // 0-9
}

export interface CurriculumUnitStatisticsDto {
  finalGrades: number[]
  assessmentAverages: AssessmentAverageDto[]
  revisionStatistics: RevisionStatisticsDto
}