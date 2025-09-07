
export interface AssistantGradingTaskDto {
  assessmentId: number
  assessmentType: 'TEST' | 'PROJECT'
  assessmentName: string
  curriculumUnitCode: string
  curriculumUnitName: string
  dueDate: string // ISO string format
  isPastDue: boolean
  pendingCount: number
  totalCount: number
}

export interface AssistantStatisticsDto {
  totalCurriculumUnits: number
  testsGraded: number
  projectsGraded: number
  revisionsGraded: number
}