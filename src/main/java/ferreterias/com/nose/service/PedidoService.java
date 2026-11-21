package ferreterias.com.nose.service;

import ferreterias.com.nose.dto.ItemPedidoRequest;
import ferreterias.com.nose.dto.PedidoRequest;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.exception.ReglaNegocioException;
import ferreterias.com.nose.model.DetallePedido;
import ferreterias.com.nose.model.Pedido;
import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.PedidoRepository;
import ferreterias.com.nose.repository.ProductRepository;
import ferreterias.com.nose.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lógica de negocio de las compras. */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Registra una compra en una sola transacción:
     * 1. agrupa los productos repetidos;
     * 2. valida que exista cada producto;
     * 3. descuenta el stock con un UPDATE atómico que solo se aplica si alcanza el stock;
     * 4. calcula el total con el precio vigente (oferta si existe) y lo guarda en el pedido.
     * Si algo falla, la transacción se revierte y no se descuenta nada.
     */
    @Transactional
    public Pedido crear(Integer idCliente, PedidoRequest request) {
        User cliente = userRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado (id " + idCliente + ")"));

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (ItemPedidoRequest item : request.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
            Product producto = productRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El producto " + entrada.getKey() + " no existe."));

            int cantidad = entrada.getValue();
            if (productRepository.descontarStock(producto.getId(), cantidad) == 0) {
                throw new ReglaNegocioException("No hay stock suficiente de \"" + producto.getNombre()
                        + "\" (disponible: " + producto.getStock() + ").");
            }

            BigDecimal precio = producto.getPrecioOferta() != null
                    ? producto.getPrecioOferta()
                    : producto.getPrecio();

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setPrecioUnitario(precio);
            detalle.setCantidad(cantidad);
            pedido.getItems().add(detalle);

            total = total.add(precio.multiply(BigDecimal.valueOf(cantidad)));
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    public List<Pedido> listarDeCliente(Integer idCliente) {
        return pedidoRepository.findByClienteIdOrderByFechaDesc(idCliente);
    }
}