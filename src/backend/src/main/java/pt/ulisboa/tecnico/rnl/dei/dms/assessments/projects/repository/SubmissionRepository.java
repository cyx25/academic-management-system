package pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.ulisboa.tecnico.rnl.dei.dms.assessments.projects.domain.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
