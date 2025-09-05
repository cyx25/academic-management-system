import type FileDto from './FileDto'

export default interface MaterialDto {
  id: number | null
  name: string
  iconName: string
  files: FileDto[]
}

export interface CreateMaterialDto {
  name: string
  iconName: string
  curriculumUnitId: number
}