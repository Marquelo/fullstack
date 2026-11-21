package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.Pedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponse(
        Integer id,
        String fecha,
        Integer clienteId,
        String clienteNombre,
        BigDecimal total,
        List<ItemPedidoResponse> items) {

    public static PedidoResponse desde(Pedido p) {
        return new PedidoResponse(
                p.getId(),
                p.getFecha().toString(),
                p.getCliente().getId(),
                p.getCliente().getNombreCompleto(),
                p.getTotal(),
                p.getItems().stream().map(ItemPedidoResponse::desde).toList());
    }
}
