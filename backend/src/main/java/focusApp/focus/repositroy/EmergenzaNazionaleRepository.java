package focusApp.focus.repositroy;

import focusApp.focus.entities.EmergenzaNazionale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmergenzaNazionaleRepository extends JpaRepository<EmergenzaNazionale, String> {
@Override
    Optional<EmergenzaNazionale>findById(String id);
}
