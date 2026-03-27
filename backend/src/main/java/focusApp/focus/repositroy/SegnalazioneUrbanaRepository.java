package focusApp.focus.repositroy;

import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.payloads.SegnalazioneMappaDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface SegnalazioneUrbanaRepository extends JpaRepository<SegnalazioneUrbana, UUID> {@Query("SELECT new focusApp.focus.payloads.SegnalazioneMappaDTO(" +
        "segnalazione.id, segnalazione.nomeProposto, segnalazione.descrizione, segnalazione.categoria, " +
        "ST_Y(segnalazione.posizione.y), ST_X(segnalazione.posizione.x)) " +
        "FROM SegnalazioneUrbana segnalazione WHERE ...")


    List<SegnalazioneMappaDTO> findVicine(
            @Param("lat") Double lat,
            @Param("lon")Double lon,
            @Param("raggio") Double raggio,
            @Param("categorie") List<String>categorie,
           org.springframework.data.domain.Pageable pageable
    );
    //Recupera tutte le segnalazioni attive per la mappa globale
    @Query("SELECT new focusApp.focus.payloads.SegnalazioneMappaDTO(" +
            "segnalazione.id, segnalazione.nomeProposto, segnalazione.categoria, segnalazione.urlImmagineRiferimento, segnalazione.descrizione, " +
            "function('ST_Y', segnalazione.posizione.y), function('ST_X', segnalazione.posizione.x)) " +
            "FROM SegnalazioneUrbana segnalazione")
    List<SegnalazioneMappaDTO> findAllProjected(org.springframework.data.domain.Pageable pageable);@Query("SELECT new focusApp.focus.payloads.SegnalazioneMappaDTO(" +
            "segnalazione.id, segnalazione.nomeProposto, segnalazione.categoria, segnalazione.urlImmagineRiferimento, segnalazione.descrizione, " +
            "function('ST_Y', segnalazione.posizione.y), function('ST_X', segnalazione.posizione.x )) " +
            "FROM SegnalazioneUrbana segnalazione")
    //Metodo per Admin, trova solo quelle da revisionare
    List<SegnalazioneUrbana> findByStato(String stato);
}



