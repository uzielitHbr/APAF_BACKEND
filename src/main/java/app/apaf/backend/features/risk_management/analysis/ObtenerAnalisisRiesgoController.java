package app.apaf.backend.features.risk_management.analysis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class ObtenerAnalisisRiesgoController {

    private final ObtenerAnalisisRiesgoHandler handler;

    public ObtenerAnalisisRiesgoController(ObtenerAnalisisRiesgoHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/analisis")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Obtener análisis de riesgo paginado por agrupación y mes")
    public RiesgoAnalisisResponse obtenerAnalisis(
            @RequestParam String agrupacion,
            @RequestParam String mesCorte,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        if (page < 1 || size < 1 || size > 100) {
            throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException("Página o tamaño inválido");
        }
        
        YearMonth ym;
        try {
            ym = YearMonth.parse(mesCorte);
        } catch (DateTimeParseException e) {
            throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException("Formato de mesCorte inválido. Use yyyy-MM");
        }

        return handler.handle(agrupacion, ym, page, size);
    }
}
