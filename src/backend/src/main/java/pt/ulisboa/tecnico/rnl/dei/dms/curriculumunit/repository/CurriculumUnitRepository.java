package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;

@Repository
public interface CurriculumUnitRepository extends JpaRepository<CurriculumUnit, Long> {
}