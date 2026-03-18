package focusApp.focus.service;


import focusApp.focus.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final JwtUtils jwtUtils;
    //Map per gestire Token invalidi
    private final Map<String, Date> blacklist = Collections.synchronizedMap(new HashMap<>());
//Metodo per aggiungere il token alla blacklist dopo il logout
public void add(String token){
   System.out.println("<<<blacklist: tentativo di inserimento>>>");
   if (token == null || token.isEmpty()){
       System.out.println("Token nullo, operazione annullata. ");
return;
   }
   //Estrazione data di scadenza
    Date scadenza= jwtUtils.getExpirationDateFromToken(token);
   blacklist.put(token, scadenza);
   System.out.println("Token invalido fino al: " + scadenza);
}
//Verifica sel token è presente nella blacklist
    public boolean isBlacklisted(String token){
    return blacklist.containsKey(token);
    }
    // pulizia del ciclo
@Scheduled(fixedRate = 3600000)
    public void cleanExpiredToken(){
    System.out.println("<<<Logica di pulizia blacklist avviata>>");
    Date attuale = new Date();
    blacklist.entrySet().removeIf(entry -> entry.getValue().before(attuale));
    System.out.println("pulizia completata. Token rimasti in memoria: " + blacklist.size());
}
}
