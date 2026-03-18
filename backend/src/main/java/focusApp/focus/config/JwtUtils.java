package focusApp.focus.config;

import focusApp.focus.entities.Utente;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.Date;

@Component
public class JwtUtils {
    private String secretKey = "FocusApp_Segretissima_Chiave_2026_Per_Protezione_Esterna_Super_Safe_Size";
  private long expirationMs = 86400000;

  // Crea il token con i dati dell'utente
    public String generateToken(Utente utente){
        return Jwts.builder()
                .setSubject(utente.getUsername())
                .claim("id", utente.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }
 public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        }catch (Exception exception){
            System.out.println("Errore JWT: " + exception.getMessage());
            return false;
        }

     }
    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().getSubject();
 }
public Date getExpirationDateFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
}
}
