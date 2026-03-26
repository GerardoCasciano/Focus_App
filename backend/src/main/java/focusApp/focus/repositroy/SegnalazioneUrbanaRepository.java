package focusApp.focus.repositroy;

import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.payloads.SegnalazioneMappaDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface SegnalazioneUrbanaRepository extends JpaRepository<SegnalazioneUrbana, UUID> {
    @Query(value = "SELECT signal.id as id, " +
            "signal.nome_proposto as nomeProposto, " +
            "signal.categoria as categoria, " +
            "signal.posizione as posizione " +
            "FROM segnalazione_urbana signal " +
            "WHERE ST_DistanceSphere(signal.posizione, :utentePos) <= :raggio " +
            "AND signal.categoria IN :categorie " +
            "AND signal.stato = 'IN_ATTESA'",
            nativeQuery = true)

    List<SegnalazioneMappaDTO> findVicine(
            @Param("utentePos") Point utentePos,
            @Param("raggio") Double raggio,
            @Param("categorie") List<String>categorie,
           org.springframework.data.domain.Pageable pageable
    );
    //Recupera tutte le segnalazioni attive per la mappa globale
    @Query("SELECT new focusApp.focus.payloads.SegnalazioneMappaDTO(signal.id, signal.posizione, signal.categoria, 'ATTIVA') " +
            "FROM SegnalazioneUrbana signal")
    List<SegnalazioneMappaDTO> findAllProjected(org.springframework.data.domain.Pageable pageable);

    //Metodo per Admin, trova solo quelle da revisionare
    List<SegnalazioneUrbana> findByStato(String stato);
}



