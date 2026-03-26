package focusApp.focus.payloads;


import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface SegnalazioneMappaDTO {
    UUID getId();
    String getNomeProposto();
    String getCategoria();
    String getUrlImmagineRiferimento();


    Double getLat();

    Double getLon();
}
