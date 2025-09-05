import type FileDto from './FileDto';

export default interface ProjectDto {
  id: number | null;
  title: string;
  weight: number;
  dueDate: string;
  maxGroupSize: number;
  statementFile: FileDto | null;
}