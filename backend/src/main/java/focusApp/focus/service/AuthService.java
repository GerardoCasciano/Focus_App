package focusApp.focus.service;

import focusApp.focus.entities.Utente;
import focusApp.focus.exceptions.UnauthorizedException;
import focusApp.focus.payloads.UtenteLoginDTO;
import focusApp.focus.security.JWTTools;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UtenteService utenteService;
    private final PasswordEncoder passwordEncoder;
    private final JWTTools jwtTools;

    public String authenticateUtenteAndGenerateToken(UtenteLoginDTO payload){
System.out.println("Ricerca utente tramite l'email");
        Utente utente = utenteService.findByEmail(payload.email());

        if(passwordEncoder.matches(payload.password(),utente.getPassword())){
           System.out.println("Confronto password inserita");
           if(!utente.isAttivo()){
               System.out.println("Errore utente non attivo");
               throw new UnauthorizedException("Credenziali non valide, riprova.");
           }
           String accessToken = jwtTools.createToken(utente);
           String refreshToken = jwtTools.createRefreshToken(utente);
            return jwtTools.createToken(utente);
        }else{
            throw new UnauthorizedException("Credenziali non valide.");
        }

    }
}
