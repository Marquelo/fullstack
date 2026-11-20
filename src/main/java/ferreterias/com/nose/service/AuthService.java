package ferreterias.com.nose.service;

import ferreterias.com.nose.dto.AuthResponse;
import ferreterias.com.nose.dto.LoginRequest;
import ferreterias.com.nose.dto.RefreshRequest;
import ferreterias.com.nose.dto.UsuarioResponse;
import ferreterias.com.nose.exception.CredencialesInvalidasException;
import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.UserRepository;
import ferreterias.com.nose.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Inicio de sesión y renovación de tokens. */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        User usuario = userRepository.findByCorreo(request.correo().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(request.contrasena(), u.getContrasena()))
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Credenciales incorrectas. Revisa el correo y la contraseña."));
        return construirRespuesta(usuario);
    }

    /** Recibe un refresh token válido y entrega un nuevo par de tokens (rotación). */
    public AuthResponse refrescar(RefreshRequest request) {
        Claims claims;
        try {
            claims = jwtService.leer(request.refreshToken());
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException("La sesión expiró. Inicia sesión nuevamente.");
        }
        if (!jwtService.esDeTipo(claims, JwtService.TIPO_REFRESH)) {
            throw new CredencialesInvalidasException("El token entregado no es un refresh token.");
        }

        Integer idUsuario = claims.get("uid", Integer.class);
        User usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new CredencialesInvalidasException("El usuario ya no existe."));
        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(User usuario) {
        return new AuthResponse(
                jwtService.generarAccessToken(usuario),
                jwtService.generarRefreshToken(usuario),
                "Bearer",
                jwtService.getAccessExpiracionSegundos(),
                UsuarioResponse.desde(usuario));
    }
}
