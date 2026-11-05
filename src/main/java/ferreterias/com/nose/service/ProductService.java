package ferreterias.com.nose.service;

import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    public Optional<Product> update(Integer id, Product productNuevo) {
        return productRepository.findById(id).map(productExistente -> {
            productExistente.setNombre(productNuevo.getNombre());
            productExistente.setPrecio(productNuevo.getPrecio());
            productExistente.setCategoria(productNuevo.getCategoria());
            productExistente.setDescripcion(productNuevo.getDescripcion());
            return productRepository.save(productExistente);
        });
    }

    public Optional<Product> deleteById(Integer id) {
        Optional<Product> productExistente = productRepository.findById(id);
        productExistente.ifPresent(product -> productRepository.deleteById(id));
        return productExistente;
    }

    public List<Product> findByCategoriaId(Integer categoriaId) {
        return productRepository.findByCategoriaId(categoriaId);
    }

    public List<Product> findByNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }
}