package focusApp.focus.repositroy;

import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.payloads.SegnalazioneMappaDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SegnalazioneUrbanaRepository extends JpaRepository<SegnalazioneUrbana, UUID> {

    @Query(value = """
        SELECT
            segnalazione.id,
            segnalazione.nome_proposto AS nomeProposto,
            segnalazione.categoria,
            segnalazione.url_immagine_riferimento AS urlImmagineRiferimento,
            segnalazione.descrizione,
            ST_Y(segnalazione.posizione) AS lat,
            ST_X(segnalazione.posizione) AS lon
        FROM segnalazione_urbana segnalazione
        WHERE ST_DWithin(
            CAST(segnalazione.posizione AS geography),
            CAST(ST_MakePoint(:lon, :lat) AS geography),
            :raggio
        )
        AND segnalazione.categoria IN (:categorie)
        """, nativeQuery = true)
    List<SegnalazioneMappaDTO> findVicine(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("raggio") Double raggio,
            @Param("categorie") List<String> categorie,
            Pageable pageable
    );

    @Query(value = """
            SELECT
                segnalazione.id,
                segnalazione.nome_proposto AS nomeProposto,
                segnalazione.categoria,
                segnalazione.url_immagine_riferimento AS urlImmagineRiferimento,
                segnalazione.descrizione,
                ST_Y(segnalazione.posizione) AS lat,
                ST_X(segnalazione.posizione) AS lon
            FROM segnalazione_urbana segnalazione
            """, nativeQuery = true)
    List<SegnalazioneMappaDTO> findAllProjected(Pageable pageable);

    List<SegnalazioneUrbana> findByStato(String stato);
}