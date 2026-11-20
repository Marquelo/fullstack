package ferreterias.com.nose.config;

import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.RolRepository;
import ferreterias.com.nose.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Crea los tres roles del sistema y un usuario de demostración por rol
 * cuando las tablas están vacías. Contraseña de los tres usuarios: 123456
 * (solo para pruebas; cámbiala si publicas el sistema).
 */
@Component
public class DatosInicialesUsuarios implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosInicialesUsuarios(RolRepository rolRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (String nombre : List.of("administrador", "vendedor", "cliente")) {
            if (!rolRepository.existsByNombre(nombre)) {
                Rol rol = new Rol();
                rol.setNombre(nombre);
                rolRepository.save(rol);
            }
        }

        if (userRepository.count() == 0) {
            userRepository.save(usuario("Carlos Administrador", LocalDate.of(1985, 5, 15),
                    "admin@ferreteria.cl", "Av. Principal 123", "masculino", "administrador"));
            userRepository.save(usuario("Valeria Vendedora", LocalDate.of(1992, 8, 20),
                    "vendedor@ferreteria.cl", "Calle Comercial 456", "femenino", "vendedor"));
            userRepository.save(usuario("Claudio Cliente", LocalDate.of(1998, 11, 10),
                    "cliente@ferreteria.cl", "Pasaje Los Rosales 789", "masculino", "cliente"));
        }
    }

    private User usuario(String nombre, LocalDate nacimiento, String correo,
                         String direccion, String genero, String rol) {
        User u = new User();
        u.setNombreCompleto(nombre);
        u.setFechaNacimiento(nacimiento);
        u.setCorreo(correo);
        u.setDireccion(direccion);
        u.setRegion("Metropolitana");
        u.setGenero(genero);
        u.setAceptaTerminos(true);
        u.setContrasena(passwordEncoder.encode("123456"));
        u.setRol(rolRepository.findByNombre(rol).orElseThrow());
        return u;
    }
}
