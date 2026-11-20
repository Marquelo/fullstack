package ferreterias.com.nose.dto;

import java.util.Map;

/** Formato único de error que devuelve la API. */
public record ErrorRespuesta(
        int status,
        String error,
        String mensaje,
        Map<String, String> detalles,
        String timestamp) {
}
