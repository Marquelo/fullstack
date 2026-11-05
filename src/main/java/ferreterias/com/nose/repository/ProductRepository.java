package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategoriaId(Integer categoriaId);

    List<Product> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);

}