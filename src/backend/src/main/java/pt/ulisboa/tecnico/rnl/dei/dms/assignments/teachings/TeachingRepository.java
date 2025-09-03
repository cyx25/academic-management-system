package pt.ulisboa.tecnico.rnl.dei.dms.assignments.teachings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingRepository extends JpaRepository<Teaching, Long> {
}