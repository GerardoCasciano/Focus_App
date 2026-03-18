package focusApp.focus.controllers;

import focusApp.focus.entities.ElementoUrbano;
import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.UtenteRepository;
import focusApp.focus.service.CloudinaryService;
import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import focusApp.focus.service.ElementoUrbanoService;
import lombok.RequiredArgsConstructor;
import focusApp.focus.service.VisionApiService;
import focusApp.focus.service.RiconoscimentoService;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api/focus")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FocusController {

    private final ElementoUrbanoService service;
private  final RiconoscimentoService riconoscimentoService;
private  final SegnalazioneUrbanaRepository segnalazioneUrbanaRepository;
private final CloudinaryService cloudinaryService;
private  final UtenteRepository utenteRepository;
private final MessageSource messageSource;
private final VisionApiService visionApiService;

    @PostMapping("/analizza")
    public ElementoUrbano esploraZona(@RequestParam("frame") MultipartFile frame, @RequestParam Double lat, @RequestParam Double lon){
//Restituisce l'elemento riconosciuto dall'IA
        return riconoscimentoService.analizzaFrame(frame, lat, lon);
    }

    @PostMapping("/segnala")
    public ResponseEntity<String> aggiungiMonumento(
            @RequestParam String nome,
            @RequestParam String descrizione,
           @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam String utenteIdString,
            @RequestParam("immagine") MultipartFile immagine){
                //Qui si salvano i dati nella tabella SegnalazioneUrbana
        //E caricare l'immagine su un servizio cloud

        System.out.println("<<Inizio del processo di segnalazione: " + nome + " ---");
//Recupero automatico della lingua daL MENU del frontend
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println("Lingua rilevata: " + locale.getLanguage());
        // Verifica della condizione se l'immagine non è presente
        if(immagine.isEmpty()){
            System.out.println("[FAIL] Immagine mancante");
            String mess = messageSource.getMessage("segnalazione.error.immagine", null,locale);
            return ResponseEntity.badRequest().body(mess);
        }
        //Verifica per le coordinate
        if (Math.abs(lat) > 90 || Math.abs(lon) > 180) {
            System.out.println(" Coordinate fuori dal range:Latitudine" + lat + "Longitudine" + lon );
String mess = messageSource.getMessage("segnalazione.error.coordinate",null,locale);
            return ResponseEntity.badRequest().body(mess);

        }
      try{
            //Cerchiamo immagine su Cloudinary
          System.out.println("Caricamento immagine su Cloudinary...");
          String urlImmagine = cloudinaryService.uploadImage(immagine);
          System.out.println("OK! immagine caricata: " +urlImmagine);
          //verica dell'utente
          UUID utenteId = UUID.fromString(utenteIdString);
          Utente autore = utenteRepository.findById(utenteId)
                  .orElseThrow(()-> new RuntimeException("Utente non trovato"));
          System.out.println("Utente verificato: " + autore.getUsername());

          //Oggetto geografico Point e si crea il ponte con lat e lon
          org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
          org.locationtech.jts.geom.Point posizione = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(lon, lat));
          System.out.println("Ok Punto geografico creato correttamente.");

          // Crea oggetto segnalazione con i dati ricevuti
          SegnalazioneUrbana segnalazione = new SegnalazioneUrbana();
          segnalazione.setNomeProposto(nome);
          segnalazione.setDescrizione(descrizione);
          segnalazione.setPosizione(posizione);
          segnalazione.setUrlImmagineRiferimento(urlImmagine);
         segnalazione.setUtente(autore);
          segnalazioneUrbanaRepository.save(segnalazione);
         //Ricerca automatica della IA tramite immagine di cloudinary
          visionApiService.eseguiAnalisi(segnalazione.getId());
          System.out.println("FINE! segnalazione per " + nome + " salvata con successo!");
         //risposta multilingua del successo!
          String messSuccesso = messageSource.getMessage("segnalazione.success",new Object[]{nome},locale);
          return ResponseEntity.ok(messSuccesso);

      }catch (IOException exception){
          System.out.println("Errore IO" + exception.getMessage());
          return ResponseEntity.internalServerError().body("Errore nel caricamento immagine: " + exception.getMessage());
      }catch (IllegalArgumentException exception){
          System.out.println("Errore UUID , formato ID non valido: " + utenteIdString);
          return  ResponseEntity.badRequest().body("ID Utente non valido.");
      }catch (Exception exception){
          System.out.println("Errore Generico OPS.." + exception.getMessage());
          return  ResponseEntity.internalServerError().body("Si è verificato un errore nel sistema");
      }
    }
    //Endpoint GET per scaricare il pacchetto delle traduzioni
    @GetMapping("/traduzioni")
    public ResponseEntity<Map<String, String>>getTraduzioni(){
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println("Richiesta traduzioni per la lingua: " + locale.getDisplayLanguage());

        Map<String, String>tutteLeTraduzioni = new HashMap<>();

        try{
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

            Enumeration<String>chiavi = bundle.getKeys();
            while (chiavi.hasMoreElements()){
                String chiave = chiavi.nextElement();

                String valore = messageSource.getMessage(chiave, null, locale);

                tutteLeTraduzioni.put(chiave, valore);
            }
            System.out.println("Inviate" + tutteLeTraduzioni.size() + "chiavi di traduzione.");
            return  ResponseEntity.ok(tutteLeTraduzioni);
        }catch (Exception exception){
            System.out.println("Errore nel caricamento del  bundle" + exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
