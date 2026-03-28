package focusApp.focus.payloads;

public record CreaSegnalazioneDTO(
        String nomeProposto,
        String categoria,
        String descrizione,
        Double lat,
        Double lon
) {}