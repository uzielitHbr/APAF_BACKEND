package app.apaf.backend.features.follow_up_management.controller;

import app.apaf.backend.features.follow_up_management.application.snapshot.GenerarSnapshotSeguimientoHandler;
import app.apaf.backend.features.follow_up_management.application.saldo.ObtenerSaldoCarteraHandler;
import app.apaf.backend.features.follow_up_management.application.saldo.SaldoCarteraResponse;
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
public class SaldoCarteraController {

    private final GenerarSnapshotSeguimientoHandler generarSnapshotHandler;
    private final ObtenerSaldoCarteraHandler saldoHandler;

    @GetMapping("/saldo-cartera")
    public SaldoCarteraResponse obtenerSaldoCartera(@RequestParam String fechaCorte) {
        YearMonth mesCorte = YearMonth.parse(fechaCorte);
        generarSnapshotHandler.generarSiNoExiste(mesCorte);
        return saldoHandler.handle(mesCorte);
    }
}
