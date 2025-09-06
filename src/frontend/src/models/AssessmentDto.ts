export default interface AssignmentDto {
  type: 'TEST' | 'PROJECT'
  name: string
  weight: number
  grade: number | null
}