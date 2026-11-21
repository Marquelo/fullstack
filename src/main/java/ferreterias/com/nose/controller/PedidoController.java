package ferreterias.com.nose.controller;

import ferreterias.com.nose.dto.PedidoRequest;
import ferreterias.com.nose.dto.PedidoResponse;
import ferreterias.com.nose.security.UsuarioAutenticado;
import ferreterias.com.nose.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Realizar una compra (solo clientes): valida stock y lo descuenta")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(
            @AuthenticationPrincipal UsuarioAutenticado actual,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PedidoResponse.desde(pedidoService.crear(actual.id(), request)));
    }

    @Operation(summary = "Listar las compras del cliente autenticado")
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis")
    public ResponseEntity<List<PedidoResponse>> misPedidos(@AuthenticationPrincipal UsuarioAutenticado actual) {
        return ResponseEntity.ok(
                pedidoService.listarDeCliente(actual.id()).stream().map(PedidoResponse::desde).toList());
    }

    @Operation(summary = "Listar todas las ventas (vendedor y administrador)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos().stream().map(PedidoResponse::desde).toList());
    }
}
