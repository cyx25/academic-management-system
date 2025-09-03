import type PersonDto from './PersonDto'

export default interface EnrollmentDto {
  id: number
  student: PersonDto
  status: string
}