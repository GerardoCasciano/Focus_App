package focusApp.focus.service;

import com.fasterxml.jackson.databind.JsonNode;
import focusApp.focus.entities.EmergenzaNazionale;
import focusApp.focus.entities.Utente;
import focusApp.focus.exceptions.GeoLocationException;
import focusApp.focus.payloads.EmergenzaNazionaleDTO;
import focusApp.focus.payloads.EmergenzaResponse;
import focusApp.focus.repositroy.EmergenzaNazionaleRepository;
import focusApp.focus.repositroy.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmergenzaService {

    private final EmergenzaNazionaleRepository emergenzaNazionaleRepository;
    private final UtenteRepository utenteRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Recupero dei numeri di emergenza in base alla posizione dell'utente
    public EmergenzaResponse getEmergenzaPersonalizzata(UUID utenteId, Double lat, Double lon){
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(()-> new EntityNotFoundException("Utente non trovato con ID: " + utenteId));

          //Interrogazione openstreetmap
        String isoCode = recuperaIsoCodeReale(lat, lon);
        EmergenzaNazionale emergenzaNazionale = emergenzaNazionaleRepository.findById(isoCode)
                .orElseThrow(()-> new EntityNotFoundException("Servizi non consentiti per: " + isoCode));

        EmergenzaNazionaleDTO numeriDTO = new EmergenzaNazionaleDTO(
                emergenzaNazionale.getCountryCode(),
                emergenzaNazionale.getNumeroEmergenza(),
                emergenzaNazionale.getPolizia(),
                emergenzaNazionale.getAmbulanza(),
                emergenzaNazionale.getVigiliDelFuoco(),
                emergenzaNazionale.getInfoUtili()
        );
        String linguaDaInviare = utente.getLinguaPreferita();
        if (linguaDaInviare == null){
            linguaDaInviare = "it";
        }
        String messaggio = String.format("Sicurezza attiva per %s. Localizzato in: %s", utente.getUsername(), isoCode);
       return  new EmergenzaResponse(
               numeriDTO,
               linguaDaInviare,
               messaggio
       );
    }
    //Chiamata HTTP per coordinate
    private String recuperaIsoCodeReale(Double lat, Double lon){
        if(lat == null || lon == null || lat < -90 || lat > 90 || lon < -180 || lon > 180 ){
            return "IT";
        }
        try{
            String url = String.format(
                    Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&addressdetails=1",
                    lat, lon
            );
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "FocusApp_Project_Bot/1.0 (contact: email@esempio.com)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
           org.springframework.http.ResponseEntity<JsonNode>response = restTemplate.exchange(
                  url,
                  HttpMethod.GET,
                  entity,
                   JsonNode.class
           );
            JsonNode root = response.getBody();


            //Estraiamo il country_code dal JSON
            if (root != null && root.has("address")){
                return root.get("address").get("country_code").asText().toUpperCase();
            }
        } catch (Exception exception){
            System.err.println("Errore Geocoding: " + exception.getMessage());
        }
        return  "IT";
    }
}
