export default interface RevisionDto {
  id: number
  studentTesteId: number
  studentName: string
  testeTitle: string
  currentGrade: number
  justification: string
  status: 'PENDING' | 'TA_GRADED' | 'APPROVED' | 'REJECTED'
  requestDate: string
  reviewedBy?: string
  reviewDate?: string
  newGrade?: number
}

export interface CreateRevisionDto {
  studentTesteId: number
  justification: string
}