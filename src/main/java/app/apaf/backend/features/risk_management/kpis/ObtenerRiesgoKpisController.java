package app.apaf.backend.features.risk_management.kpis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/v1/riesgos/limites")
@Tag(name = "Límites de Riesgo", description = "Operaciones de análisis y configuración de límites")
public class ObtenerRiesgoKpisController {

    private final ObtenerRiesgoKpisHandler handler;

    public ObtenerRiesgoKpisController(ObtenerRiesgoKpisHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Obtener KPIs consolidados por agrupación y mes")
    public RiesgoKpisResponse obtenerKpis(
            @RequestParam String agrupacion,
            @RequestParam String mesCorte) {
        
        YearMonth ym;
        try {
            ym = YearMonth.parse(mesCorte);
        } catch (DateTimeParseException e) {
            throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException("Formato de mesCorte inválido. Use yyyy-MM");
        }

        return handler.handle(agrupacion, ym);
    }
}
