package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.StudentGroupRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.StudentTesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository.ProjectRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository.TesteRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.domain.Enrollment;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinalGradeCalculationService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentTesteRepository studentTesteRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final ProjectRepository projectRepository;
    private final TesteRepository testeRepository;

    public FinalGradeCalculationService(EnrollmentRepository enrollmentRepository,
                                      StudentTesteRepository studentTesteRepository,
                                      StudentGroupRepository studentGroupRepository,
                                      ProjectRepository projectRepository,
                                      TesteRepository testeRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentTesteRepository = studentTesteRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.projectRepository = projectRepository;
        this.testeRepository = testeRepository;
    }

    /**
     * Calculate and update final grade for a student in a curriculum unit if all conditions are met
     */
    public void calculateAndUpdateFinalGrade(Long studentId, Long curriculumUnitId) {
        System.out.println("[DEBUG] Starting final grade calculation for student " + studentId + " in unit " + curriculumUnitId);
        
        // Check if total weight equals 100%
        if (!hasTotalWeightOf100(curriculumUnitId)) {
            System.out.println("[DEBUG] Total weight is not 100%, skipping final grade calculation");
            return;
        }

        // Check if student has all assessments graded
        if (!hasAllAssessmentsGraded(studentId, curriculumUnitId)) {
            System.out.println("[DEBUG] Not all assessments are graded, skipping final grade calculation");
            return;
        }

        // Calculate weighted average
        double finalGrade = calculateWeightedAverage(studentId, curriculumUnitId);
        
        // Update enrollment
        updateEnrollmentWithFinalGrade(studentId, curriculumUnitId, finalGrade);
        
        System.out.println("[DEBUG] Final grade calculated and updated: " + finalGrade);
    }

    private boolean hasTotalWeightOf100(Long curriculumUnitId) {
        // Calculate total weight from tests
        List<Teste> tests = testeRepository.findByCurriculumUnitId(curriculumUnitId);
        int totalTestWeight = 0;
        for (Teste test : tests) {
            totalTestWeight += test.getWeight();
        }

        // Calculate total weight from projects
        List<Project> projects = projectRepository.findByCurriculumUnitId(curriculumUnitId);
        int totalProjectWeight = 0;
        for (Project project : projects) {
            totalProjectWeight += project.getWeight();
        }

        int totalWeight = totalTestWeight + totalProjectWeight;
        System.out.println("[DEBUG] Total weight check: Tests=" + totalTestWeight + 
                          ", Projects=" + totalProjectWeight + ", Total=" + totalWeight);
        
        return totalWeight == 100;
    }

    private boolean hasAllAssessmentsGraded(Long studentId, Long curriculumUnitId) {
        // Check all tests are graded
        List<StudentTeste> studentTests = studentTesteRepository.findByStudentId(studentId);
        for (StudentTeste studentTest : studentTests) {
            if (studentTest.getTeste().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                if (studentTest.getGrade() == null) {
                    System.out.println("[DEBUG] Test " + studentTest.getTeste().getTitle() + " is not graded");
                    return false;
                }
            }
        }

        // Check all projects are graded
        List<StudentGroup> studentGroups = studentGroupRepository.findByStudents_Id(studentId);
        for (StudentGroup group : studentGroups) {
            if (group.getProject().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                if (group.getGrade() == null) {
                    System.out.println("[DEBUG] Project " + group.getProject().getTitle() + " is not graded");
                    return false;
                }
            }
        }

        System.out.println("[DEBUG] All assessments are graded");
        return true;
    }

    private double calculateWeightedAverage(Long studentId, Long curriculumUnitId) {
        double totalWeightedScore = 0.0;
        int totalWeight = 0;

        // Calculate weighted score from tests
        List<StudentTeste> studentTests = studentTesteRepository.findByStudentId(studentId);
        for (StudentTeste studentTest : studentTests) {
            if (studentTest.getTeste().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                Float grade = studentTest.getGrade();
                Integer weight = studentTest.getTeste().getWeight().intValue();
                if (grade != null && weight != null) {
                    totalWeightedScore += (grade * weight);
                    totalWeight += weight;
                    System.out.println("[DEBUG] Test: " + studentTest.getTeste().getTitle() + 
                                     ", Grade: " + grade + ", Weight: " + weight);
                }
            }
        }

        // Calculate weighted score from projects
        List<StudentGroup> studentGroups = studentGroupRepository.findByStudents_Id(studentId);
        for (StudentGroup group : studentGroups) {
            if (group.getProject().getCurriculumUnit().getId().equals(curriculumUnitId)) {
                Float grade = group.getGrade();
                Integer weight = group.getProject().getWeight().intValue();
                if (grade != null && weight != null) {
                    totalWeightedScore += (grade * weight);
                    totalWeight += weight;
                    System.out.println("[DEBUG] Project: " + group.getProject().getTitle() + 
                                     ", Grade: " + grade + ", Weight: " + weight);
                }
            }
        }

        double finalGrade = totalWeightedScore / totalWeight;
        System.out.println("[DEBUG] Weighted calculation: TotalWeightedScore=" + totalWeightedScore + 
                          ", TotalWeight=" + totalWeight + ", FinalGrade=" + finalGrade);
        
        return finalGrade;
    }

    private void updateEnrollmentWithFinalGrade(Long studentId, Long curriculumUnitId, double finalGrade) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository
                .findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId);

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setFinalGrade((int) Math.round(finalGrade));
            
            // Update status based on grade
            if (finalGrade >= 10.0) {
                enrollment.setStatus(Enrollment.EnrollmentStatus.APPROVED);
                System.out.println("[DEBUG] Student approved with grade: " + Math.round(finalGrade));
            } else {
                enrollment.setStatus(Enrollment.EnrollmentStatus.FAILED);
                System.out.println("[DEBUG] Student failed with grade: " + Math.round(finalGrade));
            }
            
            enrollmentRepository.save(enrollment);
        }
    }
}