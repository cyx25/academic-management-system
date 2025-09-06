export default interface StudentTesteDto {
  id: number
  studentId: number
  studentName: string
  testeId: number
  testeTitle: string
  grade: number | null
  gradedTeacherId: number | null
  gradedTeacherName: string | null
  hasRevision: boolean
  canRequestRevision: boolean
  isPendingRevision: boolean
}