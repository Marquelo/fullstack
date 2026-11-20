package ferreterias.com.nose.dto;

import ferreterias.com.nose.model.User;

import java.time.LocalDate;
import java.time.Period;

/** Datos del usuario que se envían al frontend (nunca incluye la contraseña). */
public record UsuarioResponse(
        Integer id,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        int edad,
        String correo,
        String direccion,
        String region,
        String genero,
        String rol) {

    public static UsuarioResponse desde(User u) {
        int edad = u.getFechaNacimiento() == null
                ? 0
                : Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears();
        String rol = u.getRol() == null ? null : u.getRol().getNombre();
        return new UsuarioResponse(
                u.getId(),
                u.getNombreCompleto(),
                u.getFechaNacimiento(),
                edad,
                u.getCorreo(),
                u.getDireccion(),
                u.getRegion(),
                u.getGenero(),
                rol);
    }
}
