package ferreterias.com.nose.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/** Datos para registrar o editar un usuario. */
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombreCompleto,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo válido")
        String correo,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotBlank(message = "La región es obligatoria")
        String region,

        @NotBlank(message = "El género es obligatorio")
        String genero,

        /** administrador | vendedor | cliente. Solo lo respeta un administrador. */
        String rol,

        /** Obligatoria al crear; al editar, si viene vacía se mantiene la actual. */
        String contrasena,

        boolean aceptaTerminos) {
}
