package focusApp.focus.controllers;

import ai.djl.modality.cv.Image;
import focusApp.focus.exceptions.BadRequestException;
import focusApp.focus.payloads.SegnalazioneMappaDTO;
import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import focusApp.focus.service.SegnalazioneService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/mappa")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")

public class SegnalazioniPerMappaController {

    private  final SegnalazioneUrbanaRepository segnalazioneUrbanaRepository;
private final GeometryFactory geometryFactory;
private final SegnalazioneService segnalazioneService;

    @GetMapping("/segnalazioni")
    public ResponseEntity<List<SegnalazioneMappaDTO>> getSegnalazioni(
@RequestParam(defaultValue = "100") int limit)
            {

        System.out.println("Richiesta ricevuta per segnalazioni");
        try{
         Pageable limite = PageRequest.of(0, limit);
            List<SegnalazioneMappaDTO> lista = segnalazioneUrbanaRepository.findAllProjected(limite);
            System.out.println("Segnalazioni recuperate correttamente " + lista.size() + "segnalazioni. ");
            return ResponseEntity.ok(lista);
        }catch(Exception exception){
            System.err.println("errore durante il recupero delle segnalazioni: " + exception.getMessage());
          return  ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/vicine")
    public ResponseEntity<List<SegnalazioneMappaDTO>> getSegnalazioniVicine(
            @RequestParam(required = false)Double lat,
            @RequestParam(required = false)Double lon,
            @RequestParam(defaultValue = "1000") Double raggio,
            @RequestParam (required = false, defaultValue = "TUTTE")List<String> categorie,
            @RequestParam(defaultValue = "50") int maxRisultati

    ){
        System.out.println("Ricerca punti vicini");

        //Validazione coordinate
        if(Math.abs(lat) > 90 || Math.abs(lon) > 180){
            System.out.println("Richiesta fallita fuori dal range (Lat: " + lat + ", lon" + lon + ")");
            throw  new BadRequestException("Coordinate GPS non valide");
        }
        try{
            // Creazione Dinamica dei punti
            Point utentePos = geometryFactory.createPoint(new Coordinate(lon, lat));
            org.springframework.data.domain.Pageable limite = org.springframework.data.domain.PageRequest.of(0, maxRisultati);
            List<SegnalazioneMappaDTO> vicine= segnalazioneUrbanaRepository.findVicine(utentePos, raggio, categorie,limite);
            System.err.println("Trovate " + vicine.size() + "segnalazioni nel raggio di " + raggio + "metri");
            return  ResponseEntity.ok(vicine);
        }catch(Exception exception){
            System.err.println("Errore nella query PostGis: " + exception.getMessage() );
            return  ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping(value = "/crea", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String>creaSegnalazione(
            @RequestPart("dati") SegnalazioneMappaDTO dto,
            @RequestPart("immagine") org.springframework.web.multipart.MultipartFile file
    ){
        try {
            segnalazioneService.salvaSegnalazione(dto, file);
            return ResponseEntity.ok("Segnalazione creata con successo! ");
        }catch (IOException exception){
            System.err.println("Errore durante il caricamento immagine: " +exception.getMessage());
            return  ResponseEntity.internalServerError().body("Errore nel caricamento dell'immagine.");
        }
    }
}
