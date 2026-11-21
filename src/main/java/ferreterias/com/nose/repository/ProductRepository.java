package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Descuenta stock de forma atómica: la condición "stock >= cantidad" se evalúa dentro del propio
     * UPDATE, así dos compras simultáneas no pueden dejar el stock en negativo.
     * Devuelve 0 si no alcanzó el stock (no se modifica nada) y 1 si se descontó.
     * Funciona igual en MySQL y MariaDB (no usa bloqueos con sintaxis propia de cada motor).
     */
    @Modifying(flushAutomatically = true)
    @Query("update Product p set p.stock = p.stock - :cantidad where p.id = :id and p.stock >= :cantidad")
    int descontarStock(@Param("id") Integer id, @Param("cantidad") Integer cantidad);

}