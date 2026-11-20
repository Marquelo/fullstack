package ferreterias.com.nose.controller;

import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Roles")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(summary = "Listar roles")
    @GetMapping
    public ResponseEntity<List<Rol>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @Operation(summary = "Obtener rol por id")
    @GetMapping("/{id}")
    public ResponseEntity<Rol> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @Operation(summary = "Buscar rol por nombre")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Rol> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(rolService.findByNombre(nombre));
    }

    @Operation(summary = "Crear rol")
    @PostMapping
    public ResponseEntity<Rol> save(@Valid @RequestBody Rol rol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.save(rol));
    }

    @Operation(summary = "Actualizar rol")
    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Integer id, @Valid @RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.update(id, rol));
    }

    @Operation(summary = "Eliminar rol")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
