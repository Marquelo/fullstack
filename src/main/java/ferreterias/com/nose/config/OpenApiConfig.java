package ferreterias.com.nose.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación Swagger / OpenAPI.
 * La interfaz queda disponible en /swagger-ui/index.html y el JSON en /v3/api-docs.
 *
 * El requisito de seguridad global hace que, después de pulsar "Authorize" y pegar el
 * access token, Swagger envíe el header Authorization en todas las solicitudes.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Ferretería Los Maestros",
                version = "1.0",
                description = "Servicios REST de la tienda: productos, categorías, usuarios, roles y pedidos."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pega solo el accessToken obtenido en POST /api/auth/login (sin la palabra Bearer)")
public class OpenApiConfig {
}
