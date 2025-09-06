import type AssessmentDto from "./AssessmentDto"

export default interface FinalGradeBreakdownDto {
  studentId: number
  studentName: string
  curriculumUnitId: number
  curriculumUnitName: string
  assessments: AssessmentDto[]
  totalWeight: number
  finalGrade: number | null
  isComplete: boolean
}