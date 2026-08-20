package app.apaf.backend.features.risk_management.create_limit;

import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class CrearLimiteRiesgoController {

    private final CrearLimiteRiesgoHandler handler;

    public CrearLimiteRiesgoController(CrearLimiteRiesgoHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'RIESGOS')")
    @Operation(summary = "Crear un límite de riesgo")
    public RiesgoLimiteActionResponse crear(
            @RequestBody CrearLimiteRiesgoCommand command,
            Authentication authentication) {
        return handler.handle(command, authentication.getName());
    }
}
