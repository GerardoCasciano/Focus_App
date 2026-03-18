package focusApp.focus.payloads;

import java.time.LocalDateTime;

public record ErroreDTO(
        String message,
        LocalDateTime timestamp,
        int statusCode
) {
}
