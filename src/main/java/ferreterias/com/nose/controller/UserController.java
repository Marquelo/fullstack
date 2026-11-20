package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.UsuarioRequest;
import ferreterias.com.nose.dto.UsuarioResponse;
import ferreterias.com.nose.security.UsuarioAutenticado;
import ferreterias.com.nose.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- Perfil propio: cualquier usuario con sesión ---
    @Operation(summary = "Obtener los datos del usuario autenticado")
    @GetMapping("/yo")
    public ResponseEntity<UsuarioResponse> miPerfil(@AuthenticationPrincipal UsuarioAutenticado actual) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.findById(actual.id())));
    }

    @Operation(summary = "Actualizar el perfil del usuario autenticado (no permite cambiar el rol)")
    @PutMapping("/yo")
    public ResponseEntity<UsuarioResponse> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.actualizarPerfil(actual, request)));
    }

    // --- Gestión de usuarios ---
    @Operation(summary = "Listar usuarios (el vendedor solo ve clientes)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @RequestParam(required = false) String rol) {
        return ResponseEntity.ok(userService.listarComo(actual, rol).stream().map(UsuarioResponse::desde).toList());
    }

    @Operation(summary = "Obtener usuario por id")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.findById(id)));
    }

    @Operation(summary = "Crear usuario (el vendedor solo puede crear clientes)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<UsuarioResponse> save(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.desde(userService.crearComo(actual, request)));
    }

    @Operation(summary = "Actualizar usuario y su rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.actualizarComo(actual, id, request)));
    }

    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @PathVariable Integer id) {
        userService.eliminarComo(actual, id);
        return ResponseEntity.noContent().build();
    }
}
