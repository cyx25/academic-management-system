package pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.rnl.dei.dms.assessments.testes.domain.Teste;

import java.util.List;

@Repository
public interface TesteRepository extends JpaRepository<Teste, Long> {
    
    List<Teste> findByCurriculumUnitId(Long curriculumUnitId);
}