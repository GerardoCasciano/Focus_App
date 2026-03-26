package focusApp.focus.service;

import focusApp.focus.config.JwtUtils;
import focusApp.focus.entities.Utente;
import focusApp.focus.exceptions.UnauthorizedException;
import focusApp.focus.payloads.LoginResponseDTO;
import focusApp.focus.payloads.UtenteLoginDTO;
import focusApp.focus.repositroy.UtenteRepository;
import focusApp.focus.security.JWTTools;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponseDTO authenticateUtenteAndGenerateToken(UtenteLoginDTO payload){
System.out.println("Ricerca utente tramite l'email" + payload.email());
        Utente utente = utenteRepository.findByEmail(payload.email())
                .orElseThrow(() -> {
                    System.err.println("utente non trovato nel DB");
                    return new UnauthorizedException("User non trovato");
                });


        if(passwordEncoder.matches(payload.password(),utente.getPassword())){
           System.out.println("Confronto password inserita");
           if(!utente.isAttivo()){
               System.out.println("Errore utente non attivo");
               throw new UnauthorizedException("Credenziali non valide, riprova.");
           }
           String accessToken = jwtUtils.generateToken(utente);
           String refreshToken = jwtUtils.generateToken(utente);
            return new LoginResponseDTO(accessToken, refreshToken);
        }else{
            throw new UnauthorizedException("Credenziali non valide.");
        }

    }
}
