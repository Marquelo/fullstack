package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        BigDecimal precio,

        @Positive(message = "El precio de oferta debe ser mayor a cero")
        BigDecimal precioOferta,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Size(max = 255, message = "La ruta de la imagen no puede superar los 255 caracteres")
        String imagen,

        @NotNull(message = "La categoría es obligatoria")
        @Valid
        CategoriaRef categoria) {

    /** Solo se necesita el id de la categoría. */
    public record CategoriaRef(@NotNull(message = "El id de la categoría es obligatorio") Integer id) {}

    /** Convierte el DTO en entidad; el servicio completa la categoría real. */
    public Product aProducto() {
        Product p = new Product();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setPrecioOferta(precioOferta);
        p.setStock(stock);
        p.setImagen(imagen);
        Categoria c = new Categoria();
        c.setId(categoria.id());
        p.setCategoria(c);
        return p;
    }
}