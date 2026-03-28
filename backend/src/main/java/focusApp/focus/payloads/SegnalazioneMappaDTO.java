package focusApp.focus.payloads;

import java.util.UUID;

public interface SegnalazioneMappaDTO {
    UUID getId();
    String getNomeProposto();
    String getCategoria();
    String getUrlImmagineRiferimento();
    String getDescrizione();
    Double getLat();
    Double getLon();
}