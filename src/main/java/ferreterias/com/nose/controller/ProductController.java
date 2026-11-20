package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.StockRequest;
import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "Obtener producto por id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Crear producto")
    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @Operation(summary = "Actualizar producto")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @Operation(summary = "Actualizar solo el stock de un producto")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> actualizarStock(@PathVariable Integer id, @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(productService.actualizarStock(id, request.stock()));
    }

    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar productos de una categoría")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Product>> findByCategoriaId(@PathVariable Integer categoriaId) {
        return ResponseEntity.ok(productService.findByCategoriaId(categoriaId));
    }

    @Operation(summary = "Buscar productos por nombre")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<Product>> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(productService.findByNombre(nombre));
    }
}
