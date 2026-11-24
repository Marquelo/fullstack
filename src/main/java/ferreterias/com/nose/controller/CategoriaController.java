package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.CategoriaRequest;
import ferreterias.com.nose.dto.CategoriaResponse;
import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorías")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Listar categorías")
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll().stream().map(CategoriaResponse::desde).toList());
    }

    @Operation(summary = "Obtener categoría por id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(CategoriaResponse.desde(categoriaService.findById(id)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear categoría")
    @PostMapping
    public ResponseEntity<CategoriaResponse> save(@Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponse.desde(categoriaService.save(categoria)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar categoría")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> update(@PathVariable Integer id,
                                                    @Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return ResponseEntity.ok(CategoriaResponse.desde(categoriaService.update(id, categoria)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar categoría")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}