package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.AuthResponse;
import ferreterias.com.nose.dto.LoginRequest;
import ferreterias.com.nose.dto.RefreshRequest;
import ferreterias.com.nose.dto.UsuarioRequest;
import ferreterias.com.nose.dto.UsuarioResponse;
import ferreterias.com.nose.service.AuthService;
import ferreterias.com.nose.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "Iniciar sesión: entrega access token y refresh token (JWT)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Renovar tokens a partir de un refresh token válido")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refrescar(request));
    }

    @Operation(summary = "Registro público de un cliente")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.desde(userService.registrarPublico(request)));
    }
}
