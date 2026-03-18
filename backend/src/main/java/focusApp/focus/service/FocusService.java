package focusApp.focus.service;

import focusApp.focus.entities.SegnalazioneUrbana;
import focusApp.focus.entities.StatoSegnalazione;
import focusApp.focus.entities.Utente;
import focusApp.focus.repositroy.SegnalazioneUrbanaRepository;
import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FocusService {
private final SegnalazioneUrbanaRepository segnalazioneUrbanaRepository;
//registra un monumento non riconosciuto dalla IA
    public void nuovoMonumento(Utente utente, Point posizione, String fotoUrl, String categoria,String nome, String descrizione){
        SegnalazioneUrbana nuova = SegnalazioneUrbana.builder()
                .utente(utente)
                .posizione(posizione)
                .urlImmagineRiferimento(fotoUrl)
                .categoria(categoria)
                .nomeProposto(nome)
                .descrizione(descrizione)
                .stato(StatoSegnalazione.IN_ATTESA)
                .build();

                segnalazioneUrbanaRepository.save(nuova);
                System.out.println("Nuova segnalazione ricevuta con coordinate: " + posizione);
    }
}
