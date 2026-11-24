package ferreterias.com.nose.service;

import ferreterias.com.nose.dto.UsuarioRequest;
import ferreterias.com.nose.exception.ConflictoException;
import ferreterias.com.nose.exception.RecursoNoEncontradoException;
import ferreterias.com.nose.exception.ReglaNegocioException;
import ferreterias.com.nose.model.Rol;
import ferreterias.com.nose.model.User;
import ferreterias.com.nose.repository.PedidoRepository;
import ferreterias.com.nose.repository.RolRepository;
import ferreterias.com.nose.repository.UserRepository;
import ferreterias.com.nose.security.UsuarioAutenticado;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

/** Lógica de negocio de usuarios: registro, edición, contraseñas y reglas por rol. */
@Service
public class UserService {

    private static final int EDAD_MINIMA = 14;
    private static final List<String> DOMINIOS_AUTORIZADOS = List.of(
            "@duocuc.cl", "@profesor.duoc.cl", "@gmail.com", "@outlook.com", "@hotmail.com", "@ferreteria.cl");
    // Mínimo 8 caracteres, al menos una mayúscula y un número
    private static final Pattern CONTRASENA_SEGURA = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private static final String ADMINISTRADOR = "administrador";
    private static final String VENDEDOR = "vendedor";
    private static final String CLIENTE = "cliente";

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RolRepository rolRepository,
                       PedidoRepository pedidoRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------
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

    /** Regla por rol: el vendedor solo ve clientes; el administrador ve todos (o filtra por rol). */
    public List<User> listarComo(UsuarioAutenticado actual, String rol) {
        if (VENDEDOR.equals(actual.rol())) {
            return findByRol(CLIENTE);
        }
        return (rol == null || rol.isBlank()) ? findAll() : findByRol(rol);
    }

    // ------------------------------------------------------------------
    // Registro y altas
    // ------------------------------------------------------------------
    /** Autoregistro público: siempre crea un usuario con rol cliente y exige aceptar los términos. */
    @Transactional
    public User registrarPublico(UsuarioRequest request) {
        if (!request.aceptaTerminos()) {
            throw new ReglaNegocioException("Debes aceptar los términos y condiciones.");
        }
        return crearUsuario(request, CLIENTE);
    }

    /**
     * Alta hecha por un usuario con sesión:
     * - el vendedor solo puede crear clientes (se ignora el rol que envíe);
     * - el administrador puede asignar cualquier rol (por defecto, cliente).
     */
    @Transactional
    public User crearComo(UsuarioAutenticado actual, UsuarioRequest request) {
        if (VENDEDOR.equals(actual.rol())) {
            return crearUsuario(request, CLIENTE);
        }
        String rol = (request.rol() == null || request.rol().isBlank()) ? CLIENTE : request.rol();
        return crearUsuario(request, rol);
    }

    // ------------------------------------------------------------------
    // Edición y eliminación
    // ------------------------------------------------------------------
    /** Edición hecha por un administrador. Protege el propio rol y al último administrador. */
    @Transactional
    public User actualizarComo(UsuarioAutenticado actual, Integer id, UsuarioRequest request) {
        User existente = findById(id);
        String rolActual = nombreRol(existente);
        String rolNuevo = (request.rol() == null || request.rol().isBlank())
                ? rolActual
                : request.rol().trim().toLowerCase();

        if (!rolNuevo.equals(rolActual)) {
            if (actual.id().equals(id)) {
                throw new ReglaNegocioException("No puedes cambiar tu propio rol.");
            }
            if (ADMINISTRADOR.equals(rolActual) && esUltimoAdministrador()) {
                throw new ReglaNegocioException("Debe existir al menos un administrador en el sistema.");
            }
        }

        validarDatos(request, false);
        String correo = normalizarCorreo(request.correo());
        validarCorreoUnico(correo, id);
        aplicarDatos(existente, request, correo);
        if (!rolNuevo.equals(rolActual)) {
            existente.setRol(buscarRol(rolNuevo));
        }
        aplicarContrasena(existente, request);
        return userRepository.save(existente);
    }

    /** Cada usuario edita su propio perfil. El rol nunca se modifica desde aquí. */
    @Transactional
    public User actualizarPerfil(UsuarioAutenticado actual, UsuarioRequest request) {
        User existente = findById(actual.id());
        validarDatos(request, false);
        String correo = normalizarCorreo(request.correo());
        validarCorreoUnico(correo, actual.id());
        aplicarDatos(existente, request, correo);
        aplicarContrasena(existente, request);
        return userRepository.save(existente);
    }

    /** Reglas de negocio: no se elimina la cuenta propia, al último administrador ni a un usuario con compras. */
    public void eliminarComo(UsuarioAutenticado actual, Integer id) {
        if (actual.id().equals(id)) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta activa.");
        }
        User objetivo = findById(id);
        if (ADMINISTRADOR.equals(nombreRol(objetivo)) && esUltimoAdministrador()) {
            throw new ReglaNegocioException("Debe existir al menos un administrador en el sistema.");
        }
        if (pedidoRepository.existsByClienteId(id)) {
            throw new ConflictoException(
                    "No se puede eliminar a \"" + objetivo.getNombreCompleto()
                            + "\": tiene compras registradas en el historial de ventas.");
        }
        userRepository.delete(objetivo);
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

    /** Si se envía una contraseña nueva se guarda con hash; si viene vacía se conserva la actual. */
    private void aplicarContrasena(User usuario, UsuarioRequest request) {
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        }
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toLowerCase())
                .orElseThrow(() -> new ReglaNegocioException("El rol indicado no existe."));
    }

    private String nombreRol(User usuario) {
        return usuario.getRol() == null ? "" : usuario.getRol().getNombre();
    }

    private boolean esUltimoAdministrador() {
        return userRepository.countByRolNombre(ADMINISTRADOR) <= 1;
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
