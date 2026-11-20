package ferreterias.com.nose.service;

import ferreterias.com.nose.exception.ConflictoException;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.repository.RolRepository;
import ferreterias.com.nose.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;

    public RolService(RolRepository rolRepository, UserRepository userRepository) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Rol findById(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado (id " + id + ")"));
    }

    public Rol findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toLowerCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado: " + nombre));
    }

    @Transactional
    public Rol save(Rol rol) {
        rol.setId(null);
        rol.setNombre(rol.getNombre().trim().toLowerCase());
        validarNombreUnico(rol.getNombre(), null);
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol update(Integer id, Rol rolNuevo) {
        Rol existente = findById(id);
        String nombre = rolNuevo.getNombre().trim().toLowerCase();
        validarNombreUnico(nombre, id);
        existente.setNombre(nombre);
        return rolRepository.save(existente);
    }

    /** Regla de negocio: no se elimina un rol que tenga usuarios asignados. */
    public void deleteById(Integer id) {
        Rol existente = findById(id);
        if (userRepository.existsByRolId(id)) {
            throw new ConflictoException(
                    "No se puede eliminar el rol \"" + existente.getNombre() + "\": hay usuarios que lo tienen asignado.");
        }
        rolRepository.delete(existente);
    }

    private void validarNombreUnico(String nombre, Integer idActual) {
        rolRepository.findByNombre(nombre).ifPresent(otro -> {
            if (!otro.getId().equals(idActual)) {
                throw new ConflictoException("Ya existe un rol llamado \"" + nombre + "\".");
            }
        });
    }
}
