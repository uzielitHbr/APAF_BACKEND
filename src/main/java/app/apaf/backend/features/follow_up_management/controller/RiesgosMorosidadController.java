package app.apaf.backend.features.follow_up_management.controller;

import app.apaf.backend.features.follow_up_management.application.snapshot.GenerarSnapshotSeguimientoHandler;
import app.apaf.backend.features.follow_up_management.application.morosidad.ObtenerRiesgosMorosidadHandler;
import app.apaf.backend.features.follow_up_management.application.morosidad.RiesgosMorosidadResponse;
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
public class RiesgosMorosidadController {

    private final GenerarSnapshotSeguimientoHandler generarSnapshotHandler;
    private final ObtenerRiesgosMorosidadHandler morosidadHandler;

    @GetMapping("/riesgos-morosidad")
    public RiesgosMorosidadResponse obtenerRiesgosMorosidad(@RequestParam String fechaCorte) {
        YearMonth mesCorte = YearMonth.parse(fechaCorte);
        generarSnapshotHandler.generarSiNoExiste(mesCorte);
        return morosidadHandler.handle(mesCorte);
    }
}
