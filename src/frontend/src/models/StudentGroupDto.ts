import type PersonDto from './PersonDto';
import type SubmissionDto from './SubmissioDto';

export default interface StudentGroupDto {
  id: number;
  groupId: number;
  grade: number | null;
  members: PersonDto[];
  submissions: SubmissionDto[];
  gradedTeacherName: string
}