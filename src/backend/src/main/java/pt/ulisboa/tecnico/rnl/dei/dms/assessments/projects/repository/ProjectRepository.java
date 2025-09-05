package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Project;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCurriculumUnitId(Long unitId);
} 