package ferreterias.com.nose.controller;

import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable Integer id) {
        return categoriaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categoria> save(@RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Integer id, @RequestBody Categoria categoria) {
        return categoriaService.update(id, categoria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Categoria> deleteById(@PathVariable Integer id) {
        return categoriaService.deleteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}