package app.apaf.backend.features.quarterly_analysis.branch_proportions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import app.apaf.backend.features.quarterly_analysis.generation.GenerarAnalisisTrimestralHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analisis Trimestral", description = "Comparacion versionada entre dos meses cerrados de cartera")
@RestController
@RequestMapping("/api/v1/analisis-trimestral")
public class ObtenerProporcionesSucursalController {

    private final ObtenerProporcionesSucursalHandler handler;
    private final GenerarAnalisisTrimestralHandler generarHandler;

    public ObtenerProporcionesSucursalController(ObtenerProporcionesSucursalHandler handler, GenerarAnalisisTrimestralHandler generarHandler) {
        this.handler = handler;
        this.generarHandler = generarHandler;
    }

    @GetMapping("/sucursales/proporciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Consultar proporciones de todas las sucursales", description = "Retorna el porcentaje de aportación de cada sucursal respecto a la cartera total para un mes específico (`fechaCorte`). Este endpoint está diseñado para alimentar directamente las gráficas de distribución.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparativo encontrado"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Periodo o ejecucion inexistente"),
            @ApiResponse(responseCode = "500", description = "Datos inconsistentes")
    })
    public ProporcionesResponse obtener(
            @Parameter(description = "Fecha de corte en formato yyyy-MM. Obligatorio.") @RequestParam String fechaCorte) {
        generarHandler.generarSiNoExiste(fechaCorte);
        return handler.handle(new ObtenerProporcionesSucursalQuery(fechaCorte));
    }
}
