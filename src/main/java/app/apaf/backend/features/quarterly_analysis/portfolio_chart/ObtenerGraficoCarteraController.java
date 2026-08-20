package app.apaf.backend.features.quarterly_analysis.portfolio_chart;

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
public class ObtenerGraficoCarteraController {

    private final ObtenerGraficoCarteraHandler handler;
    private final GenerarAnalisisTrimestralHandler generarHandler;

    public ObtenerGraficoCarteraController(ObtenerGraficoCarteraHandler handler, GenerarAnalisisTrimestralHandler generarHandler) {
        this.handler = handler;
        this.generarHandler = generarHandler;
    }

    @GetMapping("/grafico-cartera")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(
        summary = "Consultar grafico de cartera total", 
        description = "Agrupa y suma los importes totales de las bandas para formar los 5 rangos del grafico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultados encontrados"),
        @ApiResponse(responseCode = "400", description = "Parametros invalidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Periodo inexistente"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public GraficoCarteraResponse obtener(
        @Parameter(description = "Fecha de corte en formato yyyy-MM. Obligatorio.") @RequestParam String fechaCorte
    ) {
        generarHandler.generarSiNoExiste(fechaCorte);
        return handler.handle(new ObtenerGraficoCarteraQuery(fechaCorte));
    }
}
