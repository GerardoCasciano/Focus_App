package focusApp.focus.controllers;

import focusApp.focus.payloads.EmergenzaResponse;
import focusApp.focus.service.EmergenzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/emergenza")
@RequiredArgsConstructor
public class EmergenzaController {
    private final EmergenzaService emergenzaService;

    @GetMapping("/sos")
    public ResponseEntity<EmergenzaResponse> getSosByPosition(
            @RequestParam UUID utenteId,
            @RequestParam Double lat,
            @RequestParam Double lon
            ){
        //Chiamata al Service per il geocoding
        EmergenzaResponse response = emergenzaService.getEmergenzaPersonalizzata(utenteId, lat, lon);
        return ResponseEntity.ok(response);
    }
}
