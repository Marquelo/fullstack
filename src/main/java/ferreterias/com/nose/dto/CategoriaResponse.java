package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.Categoria;

public record CategoriaResponse(Integer id, String nombre, String descripcion) {
    public static CategoriaResponse desde(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}