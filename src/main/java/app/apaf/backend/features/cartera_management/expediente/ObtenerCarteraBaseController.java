package app.apaf.backend.features.cartera_management.expediente;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cartera")
@RequiredArgsConstructor
@Tag(name = "Cartera", description = "API de cartera")
public class ObtenerCarteraBaseController {

    private final ObtenerCarteraBaseHandler handler;

    @GetMapping("/cliente/{numeroSocio}/base")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener cartera base", description = "Obtiene la cartera base para un mes de corte y socio dado")
    public ResponseEntity<List<CarteraBaseResponse>> obtener(@PathVariable String numeroSocio, @RequestParam String mesCorte) {
        return ResponseEntity.ok(handler.carteraHanlder(numeroSocio, mesCorte));
    }
}
