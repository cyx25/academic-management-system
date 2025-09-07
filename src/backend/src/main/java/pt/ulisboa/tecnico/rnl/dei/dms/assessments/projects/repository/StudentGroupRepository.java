package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.StudentGroup;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    List<StudentGroup> findByProjectId(Long projectId);
    Optional<StudentGroup> findByProject_IdAndStudents_Id(Long projectId, Long studentId);
    List<StudentGroup> findByStudents_Id(Long studentId);
    List<StudentGroup> findByProjectIdAndGradeIsNotNull(Long projectId);


   
}
