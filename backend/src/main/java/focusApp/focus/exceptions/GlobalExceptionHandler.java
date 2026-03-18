package focusApp.focus.exceptions;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //Gestione per JPA
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object>handleEntityNotFound(EntityNotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
//Se non trova un monumento
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object>handleNotFound(ResourceNotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    //Se l'immagine è corrotta o fallisce
    @ExceptionHandler(IAProcessingException.class)
    public ResponseEntity<Object>handleError(IAProcessingException ex){
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());

    }
    //Errore generico DB
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object>handleGeneric(Exception ex){
return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel database imprevisto." + ex.getMessage());
    }
    //Gestisce errore di Geolocalizzazione
    @ExceptionHandler(GeoLocationException.class)
    public ResponseEntity<Object>handleGeoLocationError(GeoLocationException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Problema GPS: "+ ex.getMessage());
    }
    // Metodo privato peril JSON per REACT
    private ResponseEntity<Object>buildResponse(HttpStatus status, String message){
        Map<String, Object>body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return  new ResponseEntity<>(body, status);
    }
}
