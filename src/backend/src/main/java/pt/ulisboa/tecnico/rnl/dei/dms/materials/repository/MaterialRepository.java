package pt.ulisboa.tecnico.rnl.dei.dms.materials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.rnl.dei.dms.materials.domain.Material;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByCurriculumUnitId(Long curriculumUnitId);

}