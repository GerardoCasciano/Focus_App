package focusApp.focus.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emergenze_nazionali")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergenzaNazionale {
    @Id
    @Column(length = 2)
    private  String countryCode;
    private  String numeroEmergenza;
    private String polizia;
    private String ambulanza;
    private String vigiliDelFuoco;

    private String infoUtili;
}
