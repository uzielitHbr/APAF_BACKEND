package app.apaf.backend.features.cartera_eprc.controller;

import app.apaf.backend.features.cartera_eprc.application.snapshot.GenerarSnapshotEprcHandler;
import app.apaf.backend.features.cartera_eprc.application.stratification.EstratificacionCarteraResponse;
import app.apaf.backend.features.cartera_eprc.application.stratification.ObtenerEstratificacionEprcHandler;
import app.apaf.backend.features.cartera_eprc.application.stratification.ObtenerEstratificacionEprcQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/cartera-eprc")
@RequiredArgsConstructor
@Tag(name = "Cartera EPRC", description = "API para la estratificación de cartera de crédito por intervalo de morosidad")
public class EstratificacionCarteraController {

    private final GenerarSnapshotEprcHandler generarHandler;
    private final ObtenerEstratificacionEprcHandler obtenerHandler;

    @GetMapping("/estratificacion-cartera")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
    @Operation(summary = "Obtener estratificación de cartera EPRC", description = "Devuelve el desglose de la estratificación de EPRC para un mes dado")
    @ApiResponse(responseCode = "200", description = "Estratificación de cartera EPRC obtenida exitosamente")
    @ApiResponse(responseCode = "400", description = "Fecha de corte inválida")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    @ApiResponse(responseCode = "403", description = "Prohibido")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<EstratificacionCarteraResponse> obtener(
            @Parameter(description = "Fecha de corte en formato yyyy-MM. Obligatorio.") @RequestParam String fechaCorte) {

        generarHandler.generarSiNoExiste(YearMonth.parse(fechaCorte));
        return ResponseEntity.ok(obtenerHandler.handle(new ObtenerEstratificacionEprcQuery(fechaCorte)));
    }
}
