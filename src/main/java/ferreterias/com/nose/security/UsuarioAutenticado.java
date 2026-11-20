package ferreterias.com.nose.security;

/** Datos del usuario que hizo la solicitud (se obtiene del token JWT validado). */
public record UsuarioAutenticado(Integer id, String correo, String rol) {
}
