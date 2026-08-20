package app.apaf.backend.features.quarterly_analysis.delinquency_bands;

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
public class ObtenerBandasMorosidadController {

    private final ObtenerBandasMorosidadHandler handler;
    private final GenerarAnalisisTrimestralHandler generarHandler;

    public ObtenerBandasMorosidadController(ObtenerBandasMorosidadHandler handler,
            GenerarAnalisisTrimestralHandler generarHandler) {
        this.handler = handler;
        this.generarHandler = generarHandler;
    }

    @GetMapping("/analisis-corporativo")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Consultar al analisis corporativo trimestral", description = "Retorna los importes agrupados del saldo de cartera de credito segun el tipo (Vencida o Total) para un único mes (`fechaCorte`).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparativo encontrado"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Periodo o ejecucion inexistente"),
            @ApiResponse(responseCode = "500", description = "Datos inconsistentes")
    })
    public BandasMorosidadResponse obtener(
            @Parameter(description = "Fecha de corte en formato yyyy-MM. Obligatorio.") @RequestParam String fechaCorte,
            @Parameter(description = "Clasificación de banda (vencida o total). Obligatorio.") @RequestParam String clasificacion) {
        generarHandler.generarSiNoExiste(fechaCorte);
        return handler.handle(new ObtenerBandasMorosidadQuery(fechaCorte, clasificacion));
    }
}
