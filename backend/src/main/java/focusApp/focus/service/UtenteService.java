package focusApp.focus.service;

import focusApp.focus.entities.Utente;
import focusApp.focus.exceptions.NotFoundException;
import focusApp.focus.exceptions.ResourceNotFoundException;
import focusApp.focus.repositroy.UtenteRepository;
import lombok.RequiredArgsConstructor;
import focusApp.focus.exceptions.BadRequestException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UtenteService {
    private final UtenteRepository utenteRepository;
    private  final PasswordEncoder passwordEncoder;

    public Utente findByEmail(String email){
        System.out.println("Ricerca utente per email: " + email + " ---");
        return  utenteRepository.findByEmail(email)
                .orElseThrow(()->{
                    System.err.println("utente con email " + email + "non trovato. ");
                   return  new NotFoundException("utente con email " + email + " non trovato.");
                });

    }

    public void confirmAccount(UUID token) throws BadRequestException {
        if(token == null){
            throw new BadRequestException("Il token non può essere nullo.");
        }
        Utente utente = utenteRepository.findByActivationToken(token)
                .orElseThrow(()-> new ResourceNotFoundException("token non trovato o già utilizzato."));
       utente.setAttivo(true);
       utente.setActivationToken(null);
       utenteRepository.save(utente);
       System.out.println("Account attivato con successo per: " + utente.getEmail());
    }
    public Utente registrazione(Utente nuovoUtente) throws BadRequestException {
        if(utenteRepository.existsByEmail(nuovoUtente.getEmail())){
            throw  new BadRequestException("Email già presente!");
        }
        //criptazione password
        String passwordCriptata = passwordEncoder.encode(nuovoUtente.getPassword());
        nuovoUtente.setPassword(passwordCriptata);

        nuovoUtente.setAttivo(false);
        nuovoUtente.setActivationToken(UUID.randomUUID());
        System.out.println("Salvataggio nuovo utente: " + nuovoUtente.getEmail());
        return utenteRepository.save(nuovoUtente);
    }
}

