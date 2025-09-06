import type FileDto from './FileDto'

export default interface TesteDto {
  id: number | null
  title: string
  weight: number
  date: string
  duration: string
  statementFile?: FileDto
  correctionFile?: FileDto
}

export interface CreateTesteDto {
  title: string
  weight: number
  date: string
  duration: string
}