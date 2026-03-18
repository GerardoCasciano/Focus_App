package focusApp.focus.security;

import focusApp.focus.entities.Utente;
import focusApp.focus.exceptions.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JWTTools {
    @Value("${JWT_SECRET}")
    private  String secret;
    @Value("${JWT_EXPIRATION}")
    private long accessTokenExpiration;
    @Value("${JWT_REFRESH_EXPIRATION}")
    private long refreshTokenExpiration;

    //crea il token per le API
    public String createToken(Utente utente){
        return generateToken(utente, accessTokenExpiration);

    }
    //crea il token per il rinnovo
    public String createRefreshToken(Utente utente){
        return generateToken(utente, refreshTokenExpiration);
    }
    //metodo privato generico per evitare ripetizione di codice
    private String generateToken(Utente utente, long expirationTime){
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setSubject(String.valueOf(utente.getId()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
    //verifica validità token
    public void verifyToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
        }catch (Exception exception){
            throw new UnauthorizedException("token non valido o scaduto. Effettua un nuovo login. ");
        }

    }
    //Estrae ID dal token senza dover interrogare il db
    public String extractIdFromToken(String token){
     return Jwts.parserBuilder()
             .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
             .build()
             .parseClaimsJws(token)
             .getBody()
             .getSubject();
    }
}
