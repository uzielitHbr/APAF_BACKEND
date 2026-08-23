package app.apaf.backend.features.risk_management.options_limit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Configuración de límites")
public class OpcionesLimiteController {

    private final OpcionesLimiteHandler handler;

    public OpcionesLimiteController(OpcionesLimiteHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/opciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Obtiene las opciones disponibles para una agrupacion")
    public List<OpcionLimiteDto> obtenerOpciones(@RequestParam(required = false) String agrupacion) {
        return handler.handle(agrupacion);
    }
}
