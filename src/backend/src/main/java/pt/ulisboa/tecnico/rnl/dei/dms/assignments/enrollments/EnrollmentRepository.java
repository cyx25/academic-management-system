package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCurriculumUnitId(long curriculumUnitId);
    List<Enrollment> findByStudentId(Long studentId);
    Optional<Enrollment> findByCurriculumUnitIdAndStudentId(long curriculumUnitId, long studentId);
}