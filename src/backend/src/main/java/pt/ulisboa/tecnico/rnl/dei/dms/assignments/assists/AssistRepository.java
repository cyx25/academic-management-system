package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistRepository extends JpaRepository<Assist, Long> {
    Optional<Assist> findByCurriculumUnitIdAndAssistantId(long curriculumUnitId, long assistantId);
    List<Assist> findByCurriculumUnitId(long curriculumUnitId);
    List<Assist> findByAssistantId(Long assistantId);
}