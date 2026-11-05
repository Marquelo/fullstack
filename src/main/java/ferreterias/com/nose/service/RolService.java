package ferreterias.com.nose.service;

import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Optional<Rol> findById(Integer id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> update(Integer id, Rol rolNuevo) {
        return rolRepository.findById(id).map(rolExistente -> {
            rolExistente.setNombre(rolNuevo.getNombre());
            return rolRepository.save(rolExistente);
        });
    }

    public Optional<Rol> deleteById(Integer id) {
        Optional<Rol> rolExistente = rolRepository.findById(id);
        rolExistente.ifPresent(rol -> rolRepository.deleteById(id));
        return rolExistente;
    }

    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}