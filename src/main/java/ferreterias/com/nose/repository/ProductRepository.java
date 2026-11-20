package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategoriaId(Integer categoriaId);

    List<Product> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);

    Optional<Product> findByNombreIgnoreCase(String nombre);

    boolean existsByCategoriaId(Integer categoriaId);

}
