package ferreterias.com.nose.service;

import ferreterias.com.nose.dto.UsuarioRequest;
import ferreterias.com.nose.exception.ConflictoException;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.exception.ReglaNegocioException;
import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.RolRepository;
import ferreterias.com.nose.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

/** Lógica de negocio de usuarios: registro, edición, contraseñas y reglas de roles. */
@Service
public class UserService {

    private static final int EDAD_MINIMA = 14;
    private static final List<String> DOMINIOS_AUTORIZADOS = List.of(
            "@duocuc.cl", "@profesor.duoc.cl", "@gmail.com", "@outlook.com", "@hotmail.com", "@ferreteria.cl");
    // Mínimo 8 caracteres, al menos una mayúscula y un número
    private static final Pattern CONTRASENA_SEGURA = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRol(String rol) {
        return userRepository.findByRolNombre(rol.trim().toLowerCase());
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado (id " + id + ")"));
    }

    public User findByCorreo(String correo) {
        return userRepository.findByCorreo(normalizarCorreo(correo))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + correo));
    }

    /** Autoregistro público: siempre crea un usuario con rol cliente y exige aceptar los términos. */
    @Transactional
    public User registrarPublico(UsuarioRequest request) {
        if (!request.aceptaTerminos()) {
            throw new ReglaNegocioException("Debes aceptar los términos y condiciones.");
        }
        return crearUsuario(request, "cliente");
    }

    /** Alta de usuario con el rol indicado en la solicitud (por defecto, cliente). */
    @Transactional
    public User crear(UsuarioRequest request) {
        String rol = (request.rol() == null || request.rol().isBlank()) ? "cliente" : request.rol();
        return crearUsuario(request, rol);
    }

    @Transactional
    public User actualizar(Integer id, UsuarioRequest request) {
        User existente = findById(id);
        validarDatos(request, false);

        String correo = normalizarCorreo(request.correo());
        validarCorreoUnico(correo, id);
        aplicarDatos(existente, request, correo);

        if (request.rol() != null && !request.rol().isBlank()) {
            existente.setRol(buscarRol(request.rol()));
        }
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            existente.setContrasena(passwordEncoder.encode(request.contrasena()));
        }
        return userRepository.save(existente);
    }

    public void deleteById(Integer id) {
        User existente = findById(id);
        userRepository.delete(existente);
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------------------
    private User crearUsuario(UsuarioRequest request, String nombreRol) {
        validarDatos(request, true);

        String correo = normalizarCorreo(request.correo());
        validarCorreoUnico(correo, null);

        User usuario = new User();
        aplicarDatos(usuario, request, correo);
        usuario.setAceptaTerminos(request.aceptaTerminos());
        usuario.setRol(buscarRol(nombreRol));
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        return userRepository.save(usuario);
    }

    private void aplicarDatos(User usuario, UsuarioRequest request, String correoNormalizado) {
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setCorreo(correoNormalizado);
        usuario.setDireccion(request.direccion().trim());
        usuario.setRegion(request.region());
        usuario.setGenero(request.genero());
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toLowerCase())
                .orElseThrow(() -> new ReglaNegocioException("El rol indicado no existe."));
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    private void validarCorreoUnico(String correo, Integer idActual) {
        userRepository.findByCorreo(correo).ifPresent(otro -> {
            if (idActual == null || otro.getId() != idActual) {
                throw new ConflictoException("El correo electrónico ya se encuentra registrado.");
            }
        });
    }

    /**
     * Reglas de negocio de los datos del usuario:
     * edad mínima, dominio de correo autorizado y contraseña segura.
     */
    private void validarDatos(UsuarioRequest request, boolean exigirContrasena) {
        int edad = Period.between(request.fechaNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ReglaNegocioException("Debes tener al menos " + EDAD_MINIMA + " años de edad.");
        }

        String correo = normalizarCorreo(request.correo());
        if (DOMINIOS_AUTORIZADOS.stream().noneMatch(correo::endsWith)) {
            throw new ReglaNegocioException(
                    "El correo debe pertenecer a un dominio autorizado: " + String.join(", ", DOMINIOS_AUTORIZADOS) + ".");
        }

        String contrasena = request.contrasena();
        boolean informada = contrasena != null && !contrasena.isBlank();
        if (exigirContrasena || informada) {
            if (!informada || !CONTRASENA_SEGURA.matcher(contrasena).matches()) {
                throw new ReglaNegocioException(
                        "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.");
            }
        }
    }
}
