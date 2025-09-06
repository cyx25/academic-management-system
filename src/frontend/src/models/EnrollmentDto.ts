
import type PersonDto from './PersonDto'

export default interface EnrollmentDto {
  id: number
  student: PersonDto
  status: 'ENROLLED' | 'APPROVED' | 'FAILED'
  finalGrade: number | null // Add this field
}