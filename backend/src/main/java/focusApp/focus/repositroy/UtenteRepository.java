package focusApp.focus.repositroy;

import focusApp.focus.entities.Utente;

import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository extends JpaRepository<Utente, UUID> {
    Optional<Utente>findByUsername(String username);
   Optional<Utente>findByActivationToken(UUID activationToken);
   Optional<Utente>findByEmail(String email);
  boolean existsById(UUID uuid);
   boolean existsByEmail(String email);

}
