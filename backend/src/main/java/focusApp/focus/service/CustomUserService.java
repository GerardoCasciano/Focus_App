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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
       Utente utente = utenteRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato:" + username));

       return new User(
               utente.getUsername(),
               utente.getPassword(),
               Collections.singletonList(new SimpleGrantedAuthority(utente.getRuolo().name()))
       );
    }
}
