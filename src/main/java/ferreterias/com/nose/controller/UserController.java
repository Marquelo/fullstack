package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.UsuarioRequest;
import ferreterias.com.nose.dto.UsuarioResponse;
import ferreterias.com.nose.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios")
@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar usuarios (opcionalmente filtrados por rol)")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll(@RequestParam(required = false) String rol) {
        var usuarios = (rol == null || rol.isBlank()) ? userService.findAll() : userService.findByRol(rol);
        return ResponseEntity.ok(usuarios.stream().map(UsuarioResponse::desde).toList());
    }

    @Operation(summary = "Obtener usuario por id")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.findById(id)));
    }

    @Operation(summary = "Crear usuario")
    @PostMapping
    public ResponseEntity<UsuarioResponse> save(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(userService.crear(request)));
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(UsuarioResponse.desde(userService.actualizar(id, request)));
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
