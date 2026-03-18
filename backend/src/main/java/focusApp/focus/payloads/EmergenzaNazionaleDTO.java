package focusApp.focus.payloads;

public record EmergenzaNazionaleDTO(
        String countryCode,
        String numeroEmergenza,
        String polizia,
        String ambulanza,
        String vigiliDelFuoco,
        String infoUtili
) {
}
