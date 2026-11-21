package ferreterias.com.nose.config;

import ferreterias.com.nose.repository.UserRepository;
import ferreterias.com.nose.security.JwtAuthFilter;
import ferreterias.com.nose.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtService jwtService,
                                           UserRepository userRepository) throws Exception {
        http
                // API sin sesiones ni cookies: la autenticación va en el token, por eso no se usa CSRF
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Login, refresh y registro público
                        .requestMatchers("/api/auth/**").permitAll()
                        // Documentación Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Página de errores de Spring: sin esto, un error interno (500) o una ruta
                        // inexistente (404) se ven como 401 y ocultan la causa real
                        .requestMatchers("/error").permitAll()
                        // El catálogo se puede consultar sin iniciar sesión
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").permitAll()
                        // Todo lo demás exige un token válido
                        .anyRequest().authenticated())
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((request, response, ex) ->
                                escribirError(response, HttpStatus.UNAUTHORIZED,
                                        "Debes iniciar sesión para acceder a este recurso."))
                        .accessDeniedHandler((request, response, ex) ->
                                escribirError(response, HttpStatus.FORBIDDEN,
                                        "No tienes permisos para realizar esta acción.")))
                .addFilterBefore(new JwtAuthFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Permite que el frontend (otro origen) consuma la API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String origenes) {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(Arrays.stream(origenes.split(",")).map(String::trim).toList());
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    private static void escribirError(HttpServletResponse response, HttpStatus estado, String mensaje)
            throws IOException {
        response.setStatus(estado.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + estado.value()
                + ",\"error\":\"" + estado.getReasonPhrase()
                + "\",\"mensaje\":\"" + mensaje + "\"}");
    }
}