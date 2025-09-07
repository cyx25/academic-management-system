export interface UnitFinalGradeDto {
  id: number
  name: string
  finalGrade: number | null
}

export interface AssessmentDeliveryDto {
  submissionTime: string
  curriculumUnitName: string
  assessmentType: string
}

export interface ProgressDto {
  totalProjects: number
  completedProjects: number
  totalTests: number
  completedTests: number
  averageGrade: number | null
  totalCurriculumUnits: number
  completedCurriculumUnits: number
}