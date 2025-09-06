export default interface PersonDto {
  id?: number
  name?: string
  email?: string
  istId?: string
  type?: 'STUDENT' | 'TEACHING_ASSISTANT' | 'MAIN_TEACHER' // Make type more specific
}