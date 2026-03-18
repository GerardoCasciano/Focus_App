package focusApp.focus.config;

import focusApp.focus.entities.RuoloUtente;
import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.UtenteRepository;
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
    public void run(String... args){
        if (utenteRepository.findByUsername("admin").isEmpty()){
            Utente admin= new  Utente();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("focus"));
            admin.setEmail("gerrycasciano88@gmail.com");
            admin.setRuolo(RuoloUtente.ROLE_ADMIN);
            admin.setAttivo(true);

            utenteRepository.save(admin);
            System.out.println("DB Inizializzato: creato utente ADMIN");
        }
    }
}
