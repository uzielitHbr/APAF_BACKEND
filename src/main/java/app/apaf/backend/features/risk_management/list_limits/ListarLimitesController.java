package app.apaf.backend.features.risk_management.list_limits;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class ListarLimitesController {
    private final ListarLimitesHandler handler;

    public ListarLimitesController(ListarLimitesHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Listar los límites configurados")
    public ListarLimitesResponse listarLimites(
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return handler.handle(agrupacion, search, page, size);
    }
}
