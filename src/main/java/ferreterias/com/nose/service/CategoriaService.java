package ferreterias.com.nose.service;

import ferreterias.com.nose.exception.ConflictoException;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.repository.CategoriaRepository;
import ferreterias.com.nose.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de las categorías. */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductRepository productRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductRepository productRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productRepository = productRepository;
    }

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria findById(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada (id " + id + ")"));
    }

    @Transactional
    public Categoria save(Categoria categoria) {
        categoria.setId(0); // el id lo genera la base de datos
        categoria.setNombre(categoria.getNombre().trim());
        validarNombreUnico(categoria.getNombre(), null);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria update(Integer id, Categoria categoriaNueva) {
        Categoria existente = findById(id);
        String nombre = categoriaNueva.getNombre().trim();
        validarNombreUnico(nombre, id);
        existente.setNombre(nombre);
        existente.setDescripcion(categoriaNueva.getDescripcion());
        return categoriaRepository.save(existente);
    }

    /** Regla de negocio: una categoría con productos asociados no se puede eliminar. */
    public void deleteById(Integer id) {
        Categoria existente = findById(id);
        if (productRepository.existsByCategoriaId(id)) {
            throw new ConflictoException(
                    "No se puede eliminar la categoría \"" + existente.getNombre() + "\": tiene productos asociados.");
        }
        categoriaRepository.delete(existente);
    }

    private void validarNombreUnico(String nombre, Integer idActual) {
        categoriaRepository.findByNombreIgnoreCase(nombre).ifPresent(otra -> {
            if (idActual == null || otra.getId() != idActual) {
                throw new ConflictoException("Ya existe una categoría llamada \"" + nombre + "\".");
            }
        });
    }
}
