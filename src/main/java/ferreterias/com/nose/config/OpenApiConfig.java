package ferreterias.com.nose.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación Swagger / OpenAPI.
 * La interfaz queda disponible en /swagger-ui/index.html y el JSON en /v3/api-docs.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API Ferretería Los Maestros",
        version = "1.0",
        description = "Servicios REST de la tienda: productos, categorías, usuarios, roles y pedidos."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido en POST /api/auth/login")
public class OpenApiConfig {
}
