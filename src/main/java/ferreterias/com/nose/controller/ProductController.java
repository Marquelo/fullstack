package ferreterias.com.nose.controller;

import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Productos")
@RestController
@RequestMapping("/api/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Listar productos")
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "Obtener producto por id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear producto")
    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @Operation(summary = "Actualizar producto")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id, @RequestBody Product product) {
        return productService.update(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteById(@PathVariable Integer id) {
        return productService.deleteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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