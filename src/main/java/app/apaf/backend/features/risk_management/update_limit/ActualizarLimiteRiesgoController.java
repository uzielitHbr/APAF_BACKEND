package app.apaf.backend.features.risk_management.update_limit;

import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class ActualizarLimiteRiesgoController {

    private final ActualizarLimiteRiesgoHandler handler;

    public ActualizarLimiteRiesgoController(ActualizarLimiteRiesgoHandler handler) {
        this.handler = handler;
    }

    @PatchMapping("/{idLimite}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Actualizar un límite de riesgo")
    public RiesgoLimiteActionResponse actualizar(
            @PathVariable UUID idLimite,
            @RequestBody ActualizarLimiteRiesgoCommand command,
            Authentication authentication) {
        return handler.handle(idLimite, command, authentication.getName());
    }
}
