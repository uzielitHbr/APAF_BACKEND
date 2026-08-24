package app.apaf.backend.features.risk_management.reactivate_limit;

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
public class ReactivarLimiteRiesgoController {

    private final ReactivarLimiteRiesgoHandler handler;

    public ReactivarLimiteRiesgoController(ReactivarLimiteRiesgoHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/{idLimite}/reactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Reactivar un límite de riesgo")
    public RiesgoLimiteActionResponse reactivar(
            @PathVariable UUID idLimite,
            @jakarta.validation.Valid @RequestBody ReactivarLimiteRiesgoCommand command,
            Authentication authentication) {
        return handler.handle(idLimite, command, authentication.getName());
    }
}
