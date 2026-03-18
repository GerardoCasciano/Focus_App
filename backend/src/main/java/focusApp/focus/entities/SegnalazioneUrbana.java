package focusApp.focus.entities;
import org.locationtech.jts.geom.Point;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "segnalazione_urbana")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SegnalazioneUrbana {
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
@Setter(AccessLevel.NONE)
@Column( updatable = false, nullable = false)
    private UUID id;

private String nomeProposto;
@Column(columnDefinition = "TEXT")
private String descrizione;
@Column(columnDefinition = "geometry(Point, 4326)")
private Point posizione;
@CreationTimestamp
@Column(updatable = false, nullable = false)
private LocalDateTime dataCreazione ;
//Salva immagine inviata dall'utente
private String urlImmagineRiferimento;
private  String immagineTarget;
    private String categoria;
@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", referencedColumnName = "id")
    private Utente utente;



@Enumerated(EnumType.STRING)
    @Builder.Default
    private StatoSegnalazione stato = StatoSegnalazione.IN_ATTESA;

}
