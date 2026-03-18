package focusApp.focus.service;

import focusApp.focus.entities.Utente;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    //invia email con ,link che punta al frontend

    public void sendConfirmationEmail(Utente utente){

        String urlFrontend = "http://localhost:5173/verify/account?token=" + utente.getActivationToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utente.getEmail());
        message.setSubject("Conferma registrazione a FocusApp");
        message.setText("Ciao " + utente.getUsername() + "!\n\n" +
                "🚀Grazie per esserti registrato. Per attivare il tuo profilo, clicca sul link qui sotto:\n" +
                urlFrontend + "\n\n" +
                "Una volta aperta la pagina, clicca sul bottone 'Conferma Account' per iniziare.");

 try{
     mailSender.send(message);
     System.out.println("Email inviata con successo a : " + utente.getEmail());
 }catch (Exception exception){
     System.out.println("Impossibile inviare l'email: " + exception.getMessage());
 }
    }
    // Avvisa Admin di una nuova richiesta d'iscrizione.
    public  void alertAdminNuovaIscrizione(Utente nuovoUtente) {
        System.out.println("!!!Monitoraggio sicurezza!!!");

        // verifica se l'oggetto utente è nullo
        if (nuovoUtente == null) {
            System.out.println("Impossibile inviare alert: Dati utente nulli.");
            return;
        }
        // verifica se l'email dell'utente è presente
        if (nuovoUtente == null || nuovoUtente.getEmail() == null ||nuovoUtente.getEmail().isEmpty()){
            System.out.println("Alert ADMIN non inviato: utente mancante.");
            return;
        }
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            // Destinatario per il controllo
            message.setTo("gerrycasciano88@gmail.com");
            message.setSubject("🚨 FocusApp Monitor: Nuova Iscrizione [" + nuovoUtente.getUsername() + "]");
             StringBuilder stringBuilder = new StringBuilder();
             stringBuilder.append("Gentile Amministratore");
             stringBuilder.append("Un nuovo utente si è appena registrato.ecco i dettagli del monitoraggio:\n\n");
             stringBuilder.append("USERNAME: ").append(nuovoUtente.getUsername()).append("\n");
             stringBuilder.append("EMAIL: ").append(nuovoUtente.getEmail()).append("\n");
             stringBuilder.append("ID SISTEMA: ").append(nuovoUtente.getId()).append("\n");
             stringBuilder.append("DATA/ORA").append(new java.util.Date()).append("\n\n");
             stringBuilder.append("Se ricevi molte di queste email in arco di tempo potresti essere sotto attacco di bot.\n");
             stringBuilder.append("Il sistema ha già inviato il link di attivazione all'utente.");
           message.setText(stringBuilder.toString());
             // Invio fisico dell'email
            mailSender.send(message);
            System.out.println("INFORMAZIONE DI SICUREZZA: ALERT inviato correttamente all'Admin.");
        }catch (Exception exception){
            System.out.println("ERRORE CRITICO: Fallimento invio ALERT Admin: " + exception.getMessage());
        }
        }
}
