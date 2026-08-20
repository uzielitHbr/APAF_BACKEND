package app.apaf.backend.features.cartera_management.totales;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cartera/totales")
@RequiredArgsConstructor
@Tag(name = "Totales Mensuales de Cartera", description = "Resumen y totales mensuales agregados de la cartera de crédito")
public class ObtenerTotalesCarteraController {

    private final ObtenerTotalesCarteraHandler handler;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener Totales Mensuales", description = "Obtiene los totales agregados de la cartera para un mes específico.")
    public ResponseEntity<CarteraTotalesResponse> obtenerTotales(@RequestParam String mesCorte) {
        return ResponseEntity.ok(handler.handle(mesCorte));
    }
}
