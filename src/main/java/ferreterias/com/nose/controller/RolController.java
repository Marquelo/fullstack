package ferreterias.com.nose.controller;

import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> findById(@PathVariable Integer id) {
        return rolService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> save(@RequestBody Rol rol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.save(rol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Integer id, @RequestBody Rol rol) {
        return rolService.update(id, rol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Rol> deleteById(@PathVariable Integer id) {
        return rolService.deleteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Rol> findByNombre(@PathVariable String nombre) {
        return rolService.findByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}