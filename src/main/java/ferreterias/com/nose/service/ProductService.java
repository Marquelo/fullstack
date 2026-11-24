package ferreterias.com.nose.service;

import ferreterias.com.nose.exception.ConflictoException;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.exception.ReglaNegocioException;
import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.repository.CategoriaRepository;
import ferreterias.com.nose.repository.DetallePedidoRepository;
import ferreterias.com.nose.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del catálogo de productos. */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ProductService(ProductRepository productRepository,
                          CategoriaRepository categoriaRepository,
                          DetallePedidoRepository detallePedidoRepository) {
        this.productRepository = productRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado (id " + id + ")"));
    }

    @Transactional
    public Product save(Product product) {
        product.setId(null); // el id lo genera la base de datos
        completarYValidar(product, null);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Integer id, Product nuevo) {
        Product existente = findById(id);
        completarYValidar(nuevo, id);
        existente.setNombre(nuevo.getNombre());
        existente.setDescripcion(nuevo.getDescripcion());
        existente.setPrecio(nuevo.getPrecio());
        existente.setPrecioOferta(nuevo.getPrecioOferta());
        existente.setStock(nuevo.getStock());
        existente.setImagen(nuevo.getImagen());
        existente.setCategoria(nuevo.getCategoria());
        return productRepository.save(existente);
    }

    @Transactional
    public Product actualizarStock(Integer id, Integer nuevoStock) {
        if (nuevoStock == null || nuevoStock < 0) {
            throw new ReglaNegocioException("El stock debe ser un número entero igual o mayor a 0.");
        }
        Product existente = findById(id);
        existente.setStock(nuevoStock);
        return productRepository.save(existente);
    }

    public void deleteById(Integer id) {
        Product existente = findById(id);
        if (detallePedidoRepository.existsByProductoId(id)) {
            throw new ConflictoException(
                    "No se puede eliminar el producto \"" + existente.getNombre()
                            + "\": tiene ventas registradas. Puedes dejar su stock en 0 para que no se pueda comprar.");
        }
        productRepository.delete(existente);
    }

    public List<Product> findByCategoriaId(Integer categoriaId) {
        return productRepository.findByCategoriaId(categoriaId);
    }

    public List<Product> findByNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Reglas de negocio de un producto:
     * - el nombre no se repite;
     * - la categoría debe existir;
     * - el precio de oferta, si se informa, debe ser menor al precio normal;
     * - el stock no puede ser negativo.
     */
    private void completarYValidar(Product producto, Integer idActual) {
        producto.setNombre(producto.getNombre().trim());

        productRepository.findByNombreIgnoreCase(producto.getNombre()).ifPresent(otro -> {
            if (!otro.getId().equals(idActual)) {
                throw new ConflictoException("Ya existe un producto con el nombre \"" + producto.getNombre() + "\".");
            }
        });

        Integer idCategoria = producto.getCategoria().getId();
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ReglaNegocioException("La categoría indicada no existe."));
        producto.setCategoria(categoria);

        if (producto.getPrecioOferta() != null
                && producto.getPrecioOferta().compareTo(producto.getPrecio()) >= 0) {
            throw new ReglaNegocioException("El precio de oferta debe ser menor al precio normal.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo.");
        }
    }
}
