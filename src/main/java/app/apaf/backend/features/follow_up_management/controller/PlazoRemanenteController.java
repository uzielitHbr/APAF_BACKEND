package app.apaf.backend.features.follow_up_management.controller;

import app.apaf.backend.features.follow_up_management.application.snapshot.GenerarSnapshotSeguimientoHandler;
import app.apaf.backend.features.follow_up_management.application.plazo.ObtenerPlazoRemanenteHandler;
import app.apaf.backend.features.follow_up_management.application.plazo.PlazoRemanenteResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@Tag(name = "Seguimiento Cartera", description = "Endpoints para el seguimiento del plazo remanente de la cartera")
@RequestMapping("/api/v1/seguimiento-cartera")
@PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'RIESGOS')")
@RequiredArgsConstructor
public class PlazoRemanenteController {

    private final GenerarSnapshotSeguimientoHandler generarSnapshotHandler;
    private final ObtenerPlazoRemanenteHandler plazoHandler;

    @GetMapping("/plazo-remanente")
    public PlazoRemanenteResponse obtenerPlazoRemanente(
            @RequestParam String fechaCorte,
            @RequestParam(defaultValue = "CONSOLIDADO") String tipo) {
        YearMonth mesCorte = YearMonth.parse(fechaCorte);
        generarSnapshotHandler.generarSiNoExiste(mesCorte);
        return plazoHandler.handle(mesCorte, tipo);
    }
}
