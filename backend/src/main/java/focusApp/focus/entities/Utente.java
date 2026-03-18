package focusApp.focus.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column( updatable = false, nullable = false)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "lingua_preferita", length = 5)
    private String linguaPreferita = "it";
    //Serve la conferma della email per la attivazione
    private UUID activationToken;
    private boolean isAttivo = false;
    @Enumerated(EnumType.STRING)
    private RuoloUtente ruolo;
    @OneToMany(mappedBy =  "utente", cascade= CascadeType.ALL)
    private List<SegnalazioneUrbana> segnalazioni;
}
