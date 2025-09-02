package pt.ulisboa.tecnico.rnl.dei.dms.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCode(String code);
}
