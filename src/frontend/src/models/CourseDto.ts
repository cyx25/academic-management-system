export default class CourseDto {
  id?: number
  name?: string
  code?: string
  duration?: number

  constructor(obj?: Partial<CourseDto>) {
    Object.assign(this, obj)
  }
}
