
import type PersonDto from './PersonDto'

export interface TeacherStudentDto {
  student: PersonDto
  curriculumUnitCodes: string[]
}

export interface PendingGradingDto {
  assessmentId: number
  assessmentType: 'TEST' | 'PROJECT'
  assessmentName: string
  curriculumUnitCode: string
  curriculumUnitName: string
  pendingCount: number
  totalCount: number
}

export interface TeacherStatisticsDto {
  totalCurriculumUnits: number
  totalProjectsCreated: number
  totalTestsCreated: number
  revisionsAccepted: number
  revisionsDenied: number
}