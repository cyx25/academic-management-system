package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.StudentTeste;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTesteRepository extends JpaRepository<StudentTeste, Long> {

    List<StudentTeste> findByTesteId(Long testeId);

    //! this does not work !

    List<StudentTeste> findByStudentId(Long studentId);

    Optional<StudentTeste> findByTesteIdAndStudentId(Long testeId, Long studentId);
    
}