# Backend — Ferretería Los Maestros

API REST hecha con **Spring Boot 4**, **Spring Data JPA (MySQL)**, **Spring Security + JWT** y documentada con **Swagger (springdoc-openapi)**.

## Requisitos
- Java 21
- MySQL en `localhost:3306` con una base de datos llamada `base_ferreteria`
  (usuario `root` sin contraseña; se cambia en `src/main/resources/application.properties`)

## Cómo ejecutarlo
```bash
# 1. Crear la base de datos (solo la primera vez)
#    En MySQL:  CREATE DATABASE base_ferreteria;

# 2. Ejecutar
./mvnw spring-boot:run        # en Windows: mvnw.cmd spring-boot:run
```
Las tablas se crean solas (`ddl-auto=update`). Al iniciar, si están vacías, se cargan **roles, categorías, 10 productos y 3 usuarios de demostración**.

| Rol | Correo | Contraseña |
|---|---|---|
| administrador | admin@ferreteria.cl | 123456 |
| vendedor | vendedor@ferreteria.cl | 123456 |
| cliente | cliente@ferreteria.cl | 123456 |

## Swagger
- Interfaz: http://localhost:8080/swagger-ui/index.html
- JSON OpenAPI: http://localhost:8080/v3/api-docs

Para probar endpoints protegidos: ejecutar `POST /api/auth/login`, copiar el `accessToken`, pulsar **Authorize** y pegarlo.

## Arquitectura
```
controller  →  service  →  repository  →  MySQL
   │             │
   dto           exception (errores JSON uniformes)
security/config: JWT, filtro, CORS, datos iniciales
```
- **model**: entidades JPA (`Product`, `Categoria`, `User`, `Rol`, `Pedido`, `DetallePedido`).
- **dto**: objetos de entrada/salida; la contraseña nunca sale en las respuestas.
- **service**: lógica de negocio y reglas.
- **controller**: endpoints REST.

## Modelo de datos
`categoria 1—N product` · `rol 1—N user` · `user 1—N pedido` · `pedido 1—N detalle_pedido N—1 product`

## Seguridad (JWT + roles)
1. `POST /api/auth/login` valida correo y contraseña (hash BCrypt) y devuelve un **access token** (15 min) y un **refresh token** (7 días), firmados con HMAC-SHA256.
2. Cada solicitud protegida envía `Authorization: Bearer <accessToken>`. El filtro `JwtAuthFilter` verifica firma, expiración y tipo de token, busca al usuario y carga su **rol actual** desde la base de datos.
3. `POST /api/auth/refresh` entrega un nuevo par de tokens a partir de un refresh token válido.
4. Los permisos se aplican con `@PreAuthorize` en los controladores y se repiten en los servicios cuando dependen del usuario (por ejemplo, un vendedor solo crea clientes).
5. Sin token válido la API responde **401**; con token pero sin permiso, **403**.
6. La clave secreta se puede definir con la variable de entorno `JWT_SECRET`.

## Endpoints y permisos
| Endpoint | Quién puede |
|---|---|
| `POST /api/auth/login`, `/refresh`, `/registro` | Público |
| `GET /api/productos`, `/api/productos/{id}`, `/categoria/{id}`, `/nombre/{n}`; `GET /api/categorias` | Público |
| `POST/PUT/DELETE /api/productos`, `/api/categorias` | administrador |
| `PATCH /api/productos/{id}/stock` | administrador, vendedor |
| `GET /api/usuarios` | administrador (todos), vendedor (solo clientes) |
| `POST /api/usuarios` | administrador (cualquier rol), vendedor (solo clientes) |
| `GET/PUT/DELETE /api/usuarios/{id}` | administrador |
| `GET/PUT /api/usuarios/yo` | cualquier usuario con sesión |
| `/api/roles/**` | administrador |
| `POST /api/pedidos`, `GET /api/pedidos/mis` | cliente |
| `GET /api/pedidos` | administrador, vendedor |

## Reglas de negocio
- Productos: nombre único, categoría existente, oferta menor al precio normal, stock ≥ 0.
- Categorías: nombre único; no se elimina una categoría con productos.
- Usuarios: correo único, edad mínima 14 años, dominio de correo autorizado, contraseña de 8+ caracteres con mayúscula y número, no se elimina la propia cuenta ni al último administrador, nadie cambia su propio rol.
- Roles: no se elimina un rol con usuarios asignados.
- Pedidos: en una sola transacción se valida el stock, se descuenta y se calcula el total con el precio vigente (oferta si existe).
- Errores: respuesta JSON uniforme `{status, error, mensaje, detalles, timestamp}`.

## Variables de configuración (`application.properties`)
| Propiedad | Descripción |
|---|---|
| `spring.datasource.*` | Conexión a MySQL |
| `app.jwt.secret` | Clave de firma (mín. 32 caracteres) |
| `app.jwt.access-expiration-ms` / `refresh-expiration-ms` | Duración de los tokens |
| `app.cors.allowed-origins` | Orígenes del frontend permitidos |
