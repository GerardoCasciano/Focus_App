package focusApp.focus.service;

import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    private final UtenteRepository utenteRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        System.out.println("DEBUG: Spring Security sta cercando l'utente con email: [" + email + "]");
       Utente utente = utenteRepository.findByEmail(email)
       .orElseThrow(() ->{
           System.err.println("DEBUG: L'email [" + email + "] NON è stata trovata nel database!");
             return  new UsernameNotFoundException("Utente non trovato:" + email);
       });
        System.out.println("DEBUG: Utente trovato! Procedo al controllo password per: " + utente.getEmail());
       return new User(
               utente.getEmail(),
               utente.getPassword(),
               Collections.singletonList(new SimpleGrantedAuthority(utente.getRuolo().name()))
       );
    }
}
