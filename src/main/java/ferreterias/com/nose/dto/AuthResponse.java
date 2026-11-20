package ferreterias.com.nose.dto;

/** Respuesta de login y refresh: par de tokens más los datos del usuario. */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn, // segundos de vida del access token
        UsuarioResponse usuario) {
}
