package focusApp.focus.service;


import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.entities.StatoSegnalazione;
import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisionApiService {
    private final SegnalazioneUrbanaRepository segnalazioneUrbanaRepository;

    //Analizza immagine
    public List<String> ottieniLabels(MultipartFile frame) {
        //analizza lista vuota per non rompere il flusso e dare errore
        List<String> labels = new ArrayList<>();

        if (frame == null || frame.isEmpty()) {
            System.err.println("IA VISION>>> Errore: Frame vuoto o nullo. ");
            return labels;
        }
        System.out.println("IA VISION>>> Avvio analisi immagine(" + frame.getSize() + " bytes)...");
        try {
            return chiamaGoogleVision(ByteString.copyFrom(frame.getBytes()));
        } catch (IOException exception) {
            System.err.println("IA VISION>>> Errore IO: " + exception.getMessage());
            return labels;
        }
    }
    @Transactional
public  void eseguiAnalisi(UUID id){
    try{

        SegnalazioneUrbana segnalazione = segnalazioneUrbanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Segnalazione non trovata"));
        //recupero i bytes dell'immagine cloudinary
        byte[] imageBytes = new URL(segnalazione.getUrlImmagineRiferimento()).openStream().readAllBytes();
        List<String>labelsIA = chiamaGoogleVision(ByteString.copyFrom(imageBytes));
        //logica di confronto tra nome e labels IA
        String nomeCercato = segnalazione.getNomeProposto().toLowerCase();
        boolean corrispondenzaTrovata = labelsIA.stream().anyMatch(label ->
                nomeCercato.contains(label) ||
                        nomeCercato.contains(label.toLowerCase()) ||
                        label.toLowerCase().contains("fountain") ||
                        label.toLowerCase().contains("fontana") ||
                        label.toLowerCase().contains("monument") ||
                        label.toLowerCase().contains("monumento") ||
                        label.toLowerCase().contains("church") ||
                        label.toLowerCase().contains("chiesa"))
                ;

     if (corrispondenzaTrovata){
         System.out.println("IA VISION>> Corrispondenza confermata per: " + segnalazione.getNomeProposto());
      String urlTarget = segnalazione.getUrlImmagineRiferimento()
              .replace("/upload/", "/upload/e_improve,f_auto,q_auto,w_1200/");
      segnalazione.setImmagineTarget(urlTarget);
     }else {
         System.out.println("IA VISION>> Avviso: L'IA non è sicura sulla immagine: " + segnalazione.getNomeProposto());
     }
     segnalazione.setStato(StatoSegnalazione.ANALIZZATA);
     segnalazioneUrbanaRepository.save(segnalazione);
    }catch (Exception exception){
        System.err.println("IA VISION>>>Errore durante l'analisi: " + exception.getMessage());
    }
    }
private List<String>chiamaGoogleVision(ByteString imgBytes){
        return new ArrayList<>();
}
}