package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findAllByOrderByFechaDesc();

    List<Pedido> findByClienteIdOrderByFechaDesc(Integer clienteId);

}
