package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.DetallePedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Integer productoId,
        String nombre,
        BigDecimal precioUnitario,
        Integer cantidad) {

    public static ItemPedidoResponse desde(DetallePedido d) {
        return new ItemPedidoResponse(
                d.getProducto().getId(),
                d.getNombreProducto(),
                d.getPrecioUnitario(),
                d.getCantidad());
    }
}
