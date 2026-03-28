package focusApp.focus.service;

import focusApp.focus.entities.ElementoUrbano;
import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.entities.StatoSegnalazione;
import focusApp.focus.payloads.CreaSegnalazioneDTO;
import focusApp.focus.repositroy.ElementoUrbanoRepository;
import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SegnalazioneService {
    private final SegnalazioneUrbanaRepository repository;
    private final ElementoUrbanoRepository elementoUrbanoRepository;
    private final CloudinaryService cloudinaryService;
    private final GeometryFactory geometryFactory;
    private final EmailService emailService;

    @Transactional
    public void approvaSegnalazione(UUID segnalazioneId) {
        SegnalazioneUrbana segnalazioneUrbana = repository.findById(segnalazioneId)
                .orElseThrow(() -> new RuntimeException("Segnalazione non trovata."));
        try {
            ElementoUrbano nuovoElemento = new ElementoUrbano();
            nuovoElemento.setNome(segnalazioneUrbana.getNomeProposto());
            nuovoElemento.setDescrizione(segnalazioneUrbana.getDescrizione());
            nuovoElemento.setPosizione(segnalazioneUrbana.getPosizione());
            nuovoElemento.setCategoria(segnalazioneUrbana.getCategoria());
            nuovoElemento.setApprovato(true);

            elementoUrbanoRepository.save(nuovoElemento);
            segnalazioneUrbana.setStato(StatoSegnalazione.APPROVATO);
            repository.delete(segnalazioneUrbana);
            System.out.println("Segnalazione " + segnalazioneId + " approvata!");
        } catch (DataAccessException exception) {
            throw new RuntimeException("Errore tecnico durante il salvataggio: " + exception.getMessage());
        }
    }

    @Transactional
    public void rifiutaSegnalazione(UUID segnalazioneId) {
        SegnalazioneUrbana segnalazioneUrbana = repository.findById(segnalazioneId)
                .orElseThrow(() -> new RuntimeException("Segnalazione non trovata."));
        segnalazioneUrbana.setStato(StatoSegnalazione.RIFIUTATO);
        repository.save(segnalazioneUrbana);
        System.out.println("Segnalazione " + segnalazioneId + " rifiutata.");
    }

    @Transactional
    public void salvaSegnalazione(CreaSegnalazioneDTO dto, MultipartFile file) throws IOException {
        // 1. Upload immagine
        String urlCaricato = cloudinaryService.uploadImage(file);

        // 2. Costruisci la geometria
        Point posizione = geometryFactory.createPoint(new Coordinate(dto.lon(), dto.lat()));
        posizione.setSRID(4326);

        // 3. Costruisci l'entità completa e salva una volta sola
        SegnalazioneUrbana nuova = new SegnalazioneUrbana();
        nuova.setNomeProposto(dto.nomeProposto());
        nuova.setCategoria(dto.categoria());
        nuova.setDescrizione(dto.descrizione());
        nuova.setUrlImmagineRiferimento(urlCaricato);
        nuova.setPosizione(posizione);
        nuova.setStato(StatoSegnalazione.IN_ATTESA);

        repository.save(nuova);

        // 4. Notifica admin
        emailService.sendApprovalToAdmin(nuova);
        System.out.println("Segnalazione salvata con successo per: " + dto.nomeProposto());
    }
}