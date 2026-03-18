package focusApp.focus.payloads;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {
}
