package ferreterias.com.nose.security;

import ferreterias.com.nose.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Se ejecuta en cada solicitud: lee el header Authorization, valida el access token
 * y deja al usuario autenticado en el contexto de seguridad.
 *
 * El rol se lee desde la base de datos (no desde el token), así un cambio de rol
 * o la eliminación de un usuario se aplica de inmediato.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7);
            try {
                Claims claims = jwtService.leer(token);
                if (jwtService.esDeTipo(claims, JwtService.TIPO_ACCESS)) {
                    Integer idUsuario = claims.get("uid", Integer.class);
                    userRepository.findById(idUsuario).ifPresent(usuario -> {
                        if (usuario.getRol() != null) {
                            String rol = usuario.getRol().getNombre();
                            UsuarioAutenticado principal =
                                    new UsuarioAutenticado(usuario.getId(), usuario.getCorreo(), rol);
                            var autenticacion = new UsernamePasswordAuthenticationToken(
                                    principal, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase())));
                            SecurityContextHolder.getContext().setAuthentication(autenticacion);
                        }
                    });
                }
            } catch (JwtException | IllegalArgumentException e) {
                // Token inválido, alterado o expirado: la solicitud sigue sin autenticar
                // y Spring Security responde 401 si el recurso lo exige.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
