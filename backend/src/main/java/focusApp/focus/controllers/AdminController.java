package focusApp.focus.controllers;


import focusApp.focus.service.SegnalazioneService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final SegnalazioneService segnalazioneService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approva/{id}")
    public ResponseEntity<?>approvaSegnalazione(@PathVariable UUID id){
        try{
            segnalazioneService.approvaSegnalazione(id);
            return ResponseEntity.ok("Approvazione completata con successo! ");
        }catch (EntityNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }catch(Exception exception){
            return ResponseEntity.status(500).body("errore imprevisto: " + exception.getMessage());
        }
    }
    @PostMapping("/rifiuta/{id}")
    public ResponseEntity<String> rifiutaSegnalazione(@PathVariable UUID id){
        try{
            segnalazioneService.rifiutaSegnalazione(id);
            return ResponseEntity.ok("Segnalazione rifiutata correttamente. ");
        }catch (Exception exception){
            return ResponseEntity.status(500).body("Errore durante il rifiuto. ");
        }
    }
}
