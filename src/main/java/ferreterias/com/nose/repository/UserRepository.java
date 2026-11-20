package ferreterias.com.nose.repository;

import ferreterias.com.nose.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<User> findByRolNombre(String nombreRol);

    long countByRolNombre(String nombreRol);

    boolean existsByRolId(Integer rolId);

}
