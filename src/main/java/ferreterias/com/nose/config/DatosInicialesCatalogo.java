package ferreterias.com.nose.config;

import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.model.Product;
import ferreterias.com.nose.repository.CategoriaRepository;
import ferreterias.com.nose.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga el catálogo inicial (categorías y productos) cuando las tablas están vacías.
 * Son los mismos datos que usa el frontend como catálogo de ejemplo.
 */
@Component
public class DatosInicialesCatalogo implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductRepository productRepository;

    public DatosInicialesCatalogo(CategoriaRepository categoriaRepository, ProductRepository productRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            categoriaRepository.saveAll(List.of(
                    categoria("Herramientas Manuales", "Martillos, destornilladores, llaves"),
                    categoria("Pinturas y Asfaltos", "Esmaltes, látex y accesorios"),
                    categoria("Materiales de Construcción", "Cemento, maderas y fijaciones"),
                    categoria("Gasfitería y Tubos", "Tuberías y conexiones de PVC/Cobre"),
                    categoria("Seguridad", "Equipos de protección personal")));
        }

        if (productRepository.count() == 0) {
            Map<String, Categoria> categorias = new HashMap<>();
            categoriaRepository.findAll().forEach(c -> categorias.put(c.getNombre(), c));

            productRepository.saveAll(List.of(
                crear("Taladro Percutor 750W",
                        "Taladro percutor de alta potencia con velocidad variable y empuñadura ergonómica.",
                        new BigDecimal("45990"), new BigDecimal("39990"), 15, categorias.get("Herramientas Manuales"), "/IMG/taladro.jpg"),
                crear("Juego de Llaves Combinadas 12 Pzas",
                        "Set de llaves de cromo vanadio de alta resistencia en organizador plástico.",
                        new BigDecimal("19990"), null, 25, categorias.get("Herramientas Manuales"), "/IMG/llaves.jpg"),
                crear("Pintura Látex Extra Cubriente 5 Gal",
                        "Pintura látex lavable de alto rendimiento para interiores y exteriores.",
                        new BigDecimal("34990"), new BigDecimal("29990"), 10, categorias.get("Pinturas y Asfaltos"), "/IMG/pintura latex.jpg"),
                crear("Esmalte Sintético Blanco 1 Gal",
                        "Esmalte anticorrosivo acabado brillante de secado rápido.",
                        new BigDecimal("18990"), null, 18, categorias.get("Pinturas y Asfaltos"), "/IMG/esmalte.jpg"),
                crear("Saco Cemento Polpaico 25 kg",
                        "Cemento especial para albañilería y hormigón de alta durabilidad.",
                        new BigDecimal("4990"), null, 50, categorias.get("Materiales de Construcción"), "/IMG/polpaico.jpg"),
                crear("Pino Oregón 2x4 3.2m",
                        "Madera de pino cepillada y seca en cámara para estructuras.",
                        new BigDecimal("6490"), null, 40, categorias.get("Materiales de Construcción"), "/IMG/pino.jpg"),
                crear("Tubo PVC Hidráulico 25mm 6m",
                        "Tubo de PVC para conducción de agua potable a presión.",
                        new BigDecimal("3290"), null, 30, categorias.get("Gasfitería y Tubos"), "/IMG/tubo.jpg"),
                crear("Cable Eléctrico EVA 2.5mm2 100m",
                        "Rollos de cable eléctrico libre de halógenos para instalaciones domiciliarias.",
                        new BigDecimal("28990"), new BigDecimal("24990"), 12, categorias.get("Gasfitería y Tubos"), "/IMG/cable.jpg"),
                crear("Cemento Melón Blanco 25 kg",
                        "Ideal para terminaciones estéticas y morteros de pega.",
                        new BigDecimal("7490"), null, 40, categorias.get("Materiales de Construcción"), "/IMG/melon.jpg"),
                crear("Mascarilla Respirador N95 (caja x10)",
                        "Protección respiratoria contra partículas finas y polvos.",
                        new BigDecimal("10990"), null, 20, categorias.get("Seguridad"), "/IMG/mascarilla.jpg")));
        }
    }

    private Categoria categoria(String nombre, String descripcion) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        return c;
    }

    private Product crear(String nombre, String descripcion, BigDecimal precio, BigDecimal precioOferta,
                          int stock, Categoria categoria, String imagen) {
        Product p = new Product();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setPrecioOferta(precioOferta);
        p.setStock(stock);
        p.setCategoria(categoria);
        p.setImagen(imagen);
        return p;
    }
}
