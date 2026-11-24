package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    boolean existsByProductoId(Integer productoId);
}