import PersonDto from './PersonDto'
import CourseDto from './CourseDto'

export default class CurriculumUnitDto {
  id?: number
  name?: string
  code?: string
  semester?: number
  ects?: number
  mainTeacher?: PersonDto
  courses?: CourseDto[]

  constructor(obj?: Partial<CurriculumUnitDto>) {
    Object.assign(this, obj)
  }
}