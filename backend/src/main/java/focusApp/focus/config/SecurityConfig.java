package focusApp.focus.config;

import focusApp.focus.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  public final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
      http
              .csrf(csrf -> csrf.disable())
              .cors(Customizer.withDefaults())
              .authorizeHttpRequests(auth-> auth
                              .requestMatchers("/api/auth/**").permitAll()
                              .requestMatchers("api/mappa/**").permitAll()
                              .requestMatchers("/api/mappa/dettaglio/**").permitAll()
                              .requestMatchers("/api/focus/analizza").permitAll()
                              .requestMatchers("/api/focus/segnala").hasAnyRole("USER", "ADMIN")
                              .requestMatchers("/api/admin/**").hasRole("ADMIN")
                              .anyRequest().authenticated()
                      )
              .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }

}
