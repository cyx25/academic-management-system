package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.revisions.domain.Revision;

import java.util.List;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
    
    List<Revision> findByStudentTesteId(Long studentTesteId);
}