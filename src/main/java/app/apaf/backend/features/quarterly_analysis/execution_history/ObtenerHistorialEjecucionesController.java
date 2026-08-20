package app.apaf.backend.features.quarterly_analysis.execution_history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analisis Trimestral", description = "Comparacion versionada entre dos meses cerrados de cartera")
@RestController
@RequestMapping("/api/v1/analisis-trimestral")
public class ObtenerHistorialEjecucionesController {

    private final ObtenerHistorialEjecucionesHandler handler;

    public ObtenerHistorialEjecucionesController(ObtenerHistorialEjecucionesHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/ejecuciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Consultar historial de ejecuciones", description = "Devuelve el listado de los mensuales generados exitosamente. Permite identificar qué meses de corte ya han sido procesados y están disponibles para ser consultados por el dashboard.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public HistorialEjecucionesResponse obtener(
            @Parameter(description = "Mes de corte en formato yyyy-MM. Obligatorio.") @RequestParam String mesCorte,
            @Parameter(description = "Número de página (1-based).") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página.") @RequestParam(defaultValue = "50") int size) {
        return handler.handle(new ObtenerHistorialEjecucionesQuery(mesCorte, page, size));
    }
}
