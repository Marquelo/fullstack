package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.ProductoRequest;
import ferreterias.com.nose.dto.ProductoResponse;
import ferreterias.com.nose.dto.StockRequest;
import ferreterias.com.nose.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Productos")
@RestController
@RequestMapping("/api/productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Listar productos")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(
                productService.findAll().stream().map(ProductoResponse::desde).toList());
    }

    @Operation(summary = "Obtener producto por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ProductoResponse.desde(productService.findById(id)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear producto")
    @PostMapping
    public ResponseEntity<ProductoResponse> save(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductoResponse.desde(productService.save(request.aProducto())));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar producto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable Integer id,
                                                   @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(
                ProductoResponse.desde(productService.update(id, request.aProducto())));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    @Operation(summary = "Actualizar solo el stock de un producto")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(@PathVariable Integer id,
                                                            @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(
                ProductoResponse.desde(productService.actualizarStock(id, request.stock())));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar productos de una categoría")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> findByCategoriaId(@PathVariable Integer categoriaId) {
        return ResponseEntity.ok(
                productService.findByCategoriaId(categoriaId).stream().map(ProductoResponse::desde).toList());
    }

    @Operation(summary = "Buscar productos por nombre")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ProductoResponse>> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(
                productService.findByNombre(nombre).stream().map(ProductoResponse::desde).toList());
    }
}