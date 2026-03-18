package focusApp.focus.payloads;

public record MonumentoDTO(
        String nome,
        String descrizione,
        Double lat,
        Double lon,
        String immagineUrl
) {
}
