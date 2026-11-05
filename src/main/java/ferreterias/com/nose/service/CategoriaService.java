package ferreterias.com.nose.service;

import ferreterias.com.nose.model.Categoria;
import ferreterias.com.nose.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> findById(Integer id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> update(Integer id, Categoria categoriaNueva) {
        return categoriaRepository.findById(id).map(categoriaExistente -> {
            categoriaExistente.setNombre(categoriaNueva.getNombre());
            categoriaExistente.setDescripcion(categoriaNueva.getDescripcion());
            return categoriaRepository.save(categoriaExistente);
        });
    }

    public Optional<Categoria> deleteById(Integer id) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        categoriaExistente.ifPresent(categoria -> categoriaRepository.deleteById(id));
        return categoriaExistente;
    }

}
