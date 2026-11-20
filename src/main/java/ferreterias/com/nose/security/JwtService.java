package ferreterias.com.nose.security;

import ferreterias.com.nose.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera y verifica los tokens JWT.
 * - access token: corta duración, se envía en cada solicitud (Authorization: Bearer ...).
 * - refresh token: larga duración, solo sirve para pedir un nuevo par de tokens.
 * Ambos se firman con HMAC-SHA256 usando una clave secreta del servidor.
 */
@Service
public class JwtService {

    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    private final SecretKey clave;
    private final long accessExpiracionMs;
    private final long refreshExpiracionMs;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.access-expiration-ms}") long accessExpiracionMs,
                      @Value("${app.jwt.refresh-expiration-ms}") long refreshExpiracionMs) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.accessExpiracionMs = accessExpiracionMs;
        this.refreshExpiracionMs = refreshExpiracionMs;
    }

    public String generarAccessToken(User usuario) {
        return construir(usuario, TIPO_ACCESS, accessExpiracionMs);
    }

    public String generarRefreshToken(User usuario) {
        return construir(usuario, TIPO_REFRESH, refreshExpiracionMs);
    }

    public long getAccessExpiracionSegundos() {
        return accessExpiracionMs / 1000;
    }

    /** Verifica la firma y la expiración. Lanza JwtException si el token no es válido. */
    public Claims leer(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esDeTipo(Claims claims, String tipo) {
        return tipo.equals(claims.get("tipo", String.class));
    }

    private String construir(User usuario, String tipo, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)
                .compact();
    }
}
