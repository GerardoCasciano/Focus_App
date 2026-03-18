package focusApp.focus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReverseGeocodingService {
    //RestTemplate per effettuare chiamate esterne HHTP
    private final RestTemplate restTemplate = new RestTemplate();

    //Legge risposta  JSON delle api
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Restituisce il countrycode in base le coordinate
    public String getCountryCode(double lat, double lon){
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
         try{
             //chiamata GET
             String response = restTemplate.getForObject(url, String.class);
             JsonNode root = objectMapper.readTree(response);
             return root.path("address").path("country_code").asText().toUpperCase();
         }catch (Exception exception){
             //fallback in caso di errore dell'API
             System.err.println("Errore geocoding" + exception.getMessage());
             return "IT";
         }
    }
}
