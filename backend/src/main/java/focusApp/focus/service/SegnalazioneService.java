package focusApp.focus.service;

import focusApp.focus.entities.ElementoUrbano;
import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.entities.StatoSegnalazione;
import focusApp.focus.repositroy.ElementoUrbanoRepository;

import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;



import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SegnalazioneService {
    private final SegnalazioneUrbanaRepository repository;
    private final ElementoUrbanoRepository elementoUrbanoRepository;
    private final CloudinaryService cloudinaryService;
    private final GeometryFactory geometryFactory;
@Transactional
public void approvaSegnalazione(UUID segnalazioneId){
    //recupero della segnalazione

    SegnalazioneUrbana segnalazioneUrbana = repository.findById(segnalazioneId)
            .orElseThrow(() -> new RuntimeException("Segnalazione non trovata. "));
    try{


//creazione elemento urbano.
    ElementoUrbano nuovoElemento = new ElementoUrbano();
    nuovoElemento.setNome(segnalazioneUrbana.getNomeProposto());
    nuovoElemento.setDescrizione(segnalazioneUrbana.getDescrizione());
    nuovoElemento.setPosizione(segnalazioneUrbana.getPosizione());
    nuovoElemento.setCategoria(segnalazioneUrbana.getCategoria());
    nuovoElemento.setApprovato(true);

    elementoUrbanoRepository.save(nuovoElemento);
    segnalazioneUrbana.setStato(StatoSegnalazione.APPROVATO);
    repository.delete(segnalazioneUrbana);
    System.out.println("Segnalazione" + segnalazioneId + "approvata!");
    }catch (DataAccessException exception){
        throw new RuntimeException("Errore tecnico durante il salvataggio del monumento: " + exception.getMessage());
    }
    }
@Transactional
    public void rifiutaSegnalazione(UUID segnalazioneId){
    SegnalazioneUrbana segnalazioneUrbana = repository.findById(segnalazioneId)
            .orElseThrow(() -> new RuntimeException("Segnalazione non trovata. "));
    segnalazioneUrbana.setStato(StatoSegnalazione.RIFIUTATO);
    repository.save(segnalazioneUrbana);
    System.out.println("Service segnalazione" + segnalazioneId + "rifiutata. ");

    }

    @Transactional
    public void salvaSegnalazione(focusApp.focus.payloads.SegnalazioneMappaDTO dto,  org.springframework.web.multipart.MultipartFile file)throws java.io.IOException{
    String urlCaricato = cloudinaryService.uploadImage(file);
    focusApp.focus.entities.SegnalazioneUrbana nuova = new focusApp.focus.entities.SegnalazioneUrbana();
      nuova.setNomeProposto(dto.getNomeProposto());
     nuova.setCategoria(dto.getCategoria());
     nuova.setUrlImmagineRiferimento(urlCaricato);
     nuova.setStato(StatoSegnalazione.IN_ATTESA);

     org.locationtech.jts.geom.Point posizione = geometryFactory.createPoint(
         new org.locationtech.jts.geom.Coordinate(dto.getLon(), dto.getLat())
             );
     posizione.setSRID(4326);
     nuova.setPosizione(posizione);
     repository.save(nuova);
     System.out.println("Segnalazione salvata con successo per: " + dto.getNomeProposto());
    }
}
