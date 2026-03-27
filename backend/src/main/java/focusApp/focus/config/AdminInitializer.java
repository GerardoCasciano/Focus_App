package focusApp.focus.config;

import focusApp.focus.entities.RuoloUtente;
import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public void run(String... args){
        String emailAdmin = "gerrycasciano88@gmail.com";

        var utenteOpt = utenteRepository.findByEmail(emailAdmin);
        if (utenteOpt.isEmpty()){
            Utente admin= new  Utente();
            admin.setUsername(emailAdmin);
            admin.setPassword(passwordEncoder.encode("focus"));
            admin.setEmail(emailAdmin);
            admin.setRuolo(RuoloUtente.ROLE_ADMIN);
            admin.setAttivo(true);

            utenteRepository.save(admin);
            System.out.println("DB Inizializzato: creato utente ADMIN");
        }else{
            Utente esistente = utenteOpt.get();
            esistente.setAttivo(true);
            System.out.println("password aggiornata ");
        }
    }
}
