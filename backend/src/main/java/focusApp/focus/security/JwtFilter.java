package focusApp.focus.security;

import focusApp.focus.config.JwtUtils;
import focusApp.focus.service.BlacklistService;
import focusApp.focus.service.CustomUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserService userDetailsService;
    private final BlacklistService blacklistService;

@Override
 protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
    return new AntPathMatcher().match("/api/auth/**", request.getServletPath());
}
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        System.out.println("Debug: Header ricevuto" + header);
        System.out.println("Richiesta ricevuta su: " + request.getServletPath());

        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);
            System.out.println("Debug: token estratto" + token);
            //Controllo token se è nella blacklist nega accesso
            if (blacklistService.isBlacklisted(token)){
                System.out.println("Debug: Token in blacklist");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (jwtUtils.validateToken(token)){
                String email = jwtUtils.getUsernameFromToken(token);
                System.out.println("Debug: Token valido per utente" + email);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Debug: Autenticazione impostata");
            }else  {
                System.out.println("Debug: Validazione fallita in JwtUtils!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
