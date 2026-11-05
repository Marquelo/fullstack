package ferreterias.com.nose.service;

import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> update(Integer id, User userNuevo) {
        return userRepository.findById(id).map(userExistente -> {
            userExistente.setNombreCompleto(userNuevo.getNombreCompleto());
            userExistente.setFechaNacimiento(userNuevo.getFechaNacimiento());
            userExistente.setCorreo(userNuevo.getCorreo());
            userExistente.setDireccion(userNuevo.getDireccion());
            userExistente.setRegion(userNuevo.getRegion());
            userExistente.setGenero(userNuevo.getGenero());
            userExistente.setContrasena(userNuevo.getContrasena());
            userExistente.setAceptaTerminos(userNuevo.isAceptaTerminos());
            return userRepository.save(userExistente);
        });
    }

    public Optional<User> deleteById(Integer id) {
        Optional<User> userExistente = userRepository.findById(id);
        userExistente.ifPresent(user -> userRepository.deleteById(id));
        return userExistente;
    }

    public Optional<User> findByCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }
}