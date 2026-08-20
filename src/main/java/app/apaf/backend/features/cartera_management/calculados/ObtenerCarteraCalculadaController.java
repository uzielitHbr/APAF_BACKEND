package app.apaf.backend.features.cartera_management.calculados;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cartera")
@RequiredArgsConstructor
@Tag(name = "Cartera", description = "API de cartera")
public class ObtenerCarteraCalculadaController {

    private final ObtenerCarteraCalculadaHandler handler;

    @GetMapping("/cliente/{numeroSocio}/calculados")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Obtener cartera calculada", description = "Obtiene la cartera calculada para un mes de corte y socio dado")
    public ResponseEntity<List<CarteraCalculadaResponse>> obtenerCalculados(@PathVariable String numeroSocio, @RequestParam String mesCorte) {
        return ResponseEntity.ok(handler.CarteraCalculadaHanlder(numeroSocio, mesCorte));
    }
}
