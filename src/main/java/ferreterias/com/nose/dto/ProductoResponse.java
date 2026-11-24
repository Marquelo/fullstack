package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.Product;
import java.math.BigDecimal;

public record ProductoResponse(
        Integer id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        BigDecimal precioOferta,
        Integer stock,
        String imagen,
        CategoriaResponse categoria) {

    public static ProductoResponse desde(Product p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getPrecioOferta(), p.getStock(), p.getImagen(),
                CategoriaResponse.desde(p.getCategoria()));
    }
}