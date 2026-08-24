package app.apaf.backend.features.risk_management.limit_history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/riesgos/limites/historial")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class ObtenerHistorialLimitesController {
    private final ObtenerHistorialLimitesHandler handler;

    public ObtenerHistorialLimitesController(ObtenerHistorialLimitesHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Obtiene el historial de modificaciones de límites")
    public ObtenerHistorialLimitesResponse obtenerHistorial(
            @RequestParam String agrupacion,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return handler.handle(agrupacion, search, page, size);
    }
}
