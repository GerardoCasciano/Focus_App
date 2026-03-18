package focusApp.focus.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;


@Entity
@Table(name="elementi_urbani")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementoUrbano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private  Long id;
    @Column(unique = true)
    private Long osmId;
    private String nome;
    private Double latitudine;
    private Double longitudine;
    private String tipo;
    private String stileArchitettonico;
    private boolean approvato ;
    @Column(columnDefinition = "TEXT")
    private String descrizione;
    private String categoria;
 @Column(columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point posizione;
    //Modello per l'IA
    private String fotoUrl;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id")
    private Utente utente;
   @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCreazione;
}
