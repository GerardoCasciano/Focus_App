package focusApp.focus.service;

import focusApp.focus.entities.ElementoUrbano;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RiconoscimentoService {
    private final ElementoUrbanoService elementoUrbanoService;
    private final VisionApiService visionApiService;
    public ElementoUrbano analizzaFrame(MultipartFile frame, Double lat, Double lon) {
        // Chiediamo al DB cosa c'è vicino
        List<ElementoUrbano> vicini = elementoUrbanoService.cercaNelleVicinanze(lat, lon);
        if (vicini.isEmpty()) {
            System.out.println("Nessun elemento trovato nel raggio GPS. ");
            return null;
        }
        //Filtro Culturale dell' IA
        List<String> categoriaCultura = List.of("CASTELLO", "MUSEO", "CHIESA", "PIAZZA", "STATUA", "VIA", "CIMITERO_STORICO");
        List<ElementoUrbano> puntiCulturali = vicini.stream()
                .filter(elemento -> categoriaCultura.contains(elemento.getCategoria().toUpperCase()) && elemento.isApprovato())
                .collect(Collectors.toList());
//Logica di selezione
        if (puntiCulturali.isEmpty()) {
            System.out.println("IA: elementi presenti ma nessuno di categoria culturale: ");
            return null;
        }
        // Analisi IA con VisionApiService
        List<String> labelsIA = visionApiService.ottieniLabels(frame);

        //Verifica e Matching
        if (!labelsIA.isEmpty()) {
            for (ElementoUrbano punto : puntiCulturali) {
                String categ = punto.getCategoria().toUpperCase();
                String nome = punto.getNome().toUpperCase();
                //Cerchiamo se una delle etichette di google è contenuta nel nome o nella categoria el DB
                boolean match = labelsIA.stream().anyMatch(labIA -> nome.contains(labIA) || categ.contains(labIA) || labIA.contains("monument"));
                if (match) {
                    System.out.println("Elemento trovato! L'utente sta guardando: " + punto.getNome());
                    return punto;
                }
            }
        }
        System.out.println("IA incerta  errore API elemento più vicino per GPS" + puntiCulturali.get(0).getNome());
return  puntiCulturali.get(0);
    }
}

