package focusApp.focus.payloads;



import java.util.UUID;

public record SegnalazioneMappaDTO(
        UUID id,
        String nomeProposto,
        String categoria,
        String urlImmagineRiferimento,
        String descrizione,
       Double lat,
        Double lon
) {}