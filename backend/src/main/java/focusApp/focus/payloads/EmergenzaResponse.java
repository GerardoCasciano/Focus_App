package focusApp.focus.payloads;

public record EmergenzaResponse(
        EmergenzaNazionaleDTO numeri,
        String linguaUtente,
        String messaggioPersonalizzato
) {
}
