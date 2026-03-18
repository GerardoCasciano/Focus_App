package focusApp.focus.controllers;

import focusApp.focus.config.JwtUtils;
import focusApp.focus.entities.RuoloUtente;
import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.UtenteRepository;
import focusApp.focus.service.BlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import focusApp.focus.service.EmailService;
import focusApp.focus.payloads.LoginRequest;


import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final BlacklistService blacklistService;
    private final EmailService emailService;

    //Registrazione: protegge i dati e invia email di conferma

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utente utente) {
        System.out.println("<<Inizio Registrazione Utente>>" + utente.getUsername() + "---");
        //Verifica se utente esiste nel DB o è troppo corto

        if (utente.getUsername() == null || utente.getUsername().length() < 4) {
            System.out.println("Registrazione fallita: Utente non valido" + utente.getUsername());
            return ResponseEntity.badRequest().body("⛔Errore:  Username deve avere almeno 4 caratteri .");
        }


        if (utenteRepository.existsByEmail(utente.getEmail())) {
            System.out.println("registrazione fallita:Email già presente");
            return ResponseEntity.badRequest().body("⛔Errore: Questa email è già registrata.");
        }
        System.out.println("Dati validi. Preparazione account inattivo...");
        //Cripta password per eventuali furti dal DB
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        //Genera token casuale unico per attivazione
        utente.setActivationToken(UUID.randomUUID());
        utente.setAttivo(false);
        utente.setRuolo(RuoloUtente.ROLE_USER);
        //Salvataggio nel DB in attesa o inattivo
        utenteRepository.save(utente);
        System.out.println("utente salvato nel DB in attesa di attivazione.");


        try{
            //Invio email all'utente con i dati inseriti
            emailService.sendConfirmationEmail(utente);
            //Invio email ad ADMIN per notificare nuovo utente
            emailService.alertAdminNuovaIscrizione(utente);
        }catch (Exception exception){
        System.err.println("Errore durante l'invio delle email: " + exception.getMessage());
        }

        System.out.println("Email inviate correttamente.");

        return ResponseEntity.ok("Registrazione effettuata! Controlla l'email per attivare il tuo account.");
    }

    //Verifica automatica
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            UUID uuidToken = UUID.fromString(token);
            //Cerco utente che hA quel token specifico
            Utente utente = utenteRepository.findByActivationToken(uuidToken)
                    .orElseThrow(null);
            //Controllo se il token è errato
            if (utente == null) {
                System.out.println("Token non valido o utente non trovato.");
                return ResponseEntity.badRequest().body("Errore: il link di attivazione è scaduto o non valido.");
            }
            //Attiva utente
            utente.setAttivo(true);
            //Cancella il token
            utente.setActivationToken(null);
            utenteRepository.save(utente);
            System.out.println("Account attivo per: " + utente.getUsername());
            return ResponseEntity.ok("Account attivato con successo! Effettua il Login!" + utente.getUsername());
        } catch (IllegalArgumentException exception) {
            System.out.println("formato token non valdo");
            return ResponseEntity.badRequest().body("Il formato del token non è valido.");
        }
    }

    // Login: Genera un nuovo token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        System.out.println("Login fallito: username non trovato");
        Utente utente = utenteRepository.findByUsername(request.getUsername()).orElse(null);
        if (utente == null) {
            System.out.println("Login fallito:username non trovato.");
            return ResponseEntity.status(401).body("⚠️Credenziali errate");
        }
        if (!utente.isAttivo()) {
            return ResponseEntity.status(403).body("⚠️Devi prima attivare l'account tramite l'email che ti abbiamo inviato");
        }

        if (!passwordEncoder.matches(request.getPassword(), utente.getPassword())) {
            System.out.println("Login fallito: password errata");
            return ResponseEntity.status(401).body("Credenziali errate.");

        }

        //se è Ok genra il jwt
        String token = jwtUtils.generateToken(utente);
        System.out.println("login completato per" + utente.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }


    // Logout: invalida il Token appena utente esce inserendolo nella blacklist
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null || !header.toLowerCase().startsWith("Bearer ")) {
            System.out.println("logout fallito: header non conforme allo standard Bearer.");
            return ResponseEntity.badRequest().body("⚠️Token mancante non valido.");
        }
        //Estrazione token
        String token = header.replaceFirst("(?i)bearer", "").trim();
        System.out.println("Token estratto correttamente: " + (token.length() > 10 ? token.substring(0, 10) + "..." : token));
        //Aggiunge alla blacklist
        blacklistService.add(token);
        System.out.println("Logout completato per il token estratto.");
        return ResponseEntity.ok("Logout effettuato.");
    }
}

