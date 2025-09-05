import type FileDto from './FileDto';

export default interface SubmissionDto {
    id: number;
    submissionFile: FileDto | null;
    grade: number | null; // Grade can be null if not graded yet
    submissionDate: string; // ISO 8601 format date-time string
    submittedBy: string; // Name or identifier of the person who submitted
}