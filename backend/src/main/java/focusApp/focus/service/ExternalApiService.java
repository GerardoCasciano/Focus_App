package focusApp.focus.service;

import focusApp.focus.entities.ElementoUrbano;
import focusApp.focus.exceptions.ExternalApiException;
import focusApp.focus.payloads.OsmElement;
import focusApp.focus.payloads.OsmResponse;
import focusApp.focus.repositroy.ElementoUrbanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
@RequiredArgsConstructor
public class ExternalApiService {
    private final RestTemplate restTemplate;
    private  final ElementoUrbanoRepository repository;

    public void popolaDatiZona(Double lat, Double lon ){
        //Query per cercare monumenti opere nel raggio di 1 km
        String query = String.format(java.util.Locale.US,"[out:json];("+ "node(around:1000,%f,%f)[historic];" +
                "node(around:2000,%f,%f)[tourism];" + ");out;", lat, lon, lat, lon);
        String url = "https://overpass-api.de/api/interpreter?data=" + query;

        try{
            //chiama API e mappa il json nel record
            OsmResponse response = restTemplate.getForObject(url, OsmResponse.class);
            //Controllo rapido se non cè risposta o elementi si ferma
        if(response == null || response.elements() == null) return;
        for (OsmElement element : response.elements()){
            String nome = (element.tags() != null) ? element.tags().get("name") : null ;
            //Se manca il nome, passiamo al prossimo elemento
            if(nome == null || nome.isEmpty()) continue;
            // Logica per decidere cosa fare in base al tipo di elemento
            if (element.tags().containsKey("historic")){
                gestisciMonumento(element);
            } else if (element.tags().containsKey("highway")) {
                gestisciStrada(element);

            }else{
                System.out.println("Elemento ignorato: " + nome);
            }
        }
        }catch (org.springframework.web.client.HttpClientErrorException exception){
            throw  new ExternalApiException("Sintassi della query non valida o errata.");

        }catch (org.springframework.web.client.ResourceAccessException exception){
            throw  new ExternalApiException("OpenStreetMap server non è raggiungibile.Riprova più tardi.");

        }catch (Exception exception){
            throw  new ExternalApiException("Errore nel recupero dati da OpenStreetMap");
        }
    }
    //Metodo di supporto
    private void gestisciMonumento(OsmElement element){
        if(repository.existsByOsmId(element.id())){
            return;
        }
        //Creo entità dai DTO
        ElementoUrbano nuovoMonumento = new ElementoUrbano();
        nuovoMonumento.setOsmId(element.id());
        nuovoMonumento.setNome(element.tags().get("name"));
        nuovoMonumento.setLatitudine(element.lat());
        nuovoMonumento.setLongitudine(element.lon());
        nuovoMonumento.setTipo("STRADA");
        nuovoMonumento.setApprovato(true);
        //Salva nel DB
 repository.save(nuovoMonumento);

 //Invio avviso ad ADMIN
        System.out.println("Nuovo monumento in attesa di essere aggiunto: " + nuovoMonumento.getNome());

    }
    private void gestisciStrada(OsmElement element){
        if(!repository.existsByOsmId(element.id())){
            ElementoUrbano strada = new ElementoUrbano();
            strada.setOsmId(element.id());
            strada.setNome(element.tags().get("name"));
            strada.setLatitudine(element.lat());
            strada.setLongitudine(element.lon());
            strada.setTipo("STRADA");
            strada.setApprovato(true);
            repository.save(strada);
        }
    }
}
