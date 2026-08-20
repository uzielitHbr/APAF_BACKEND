package app.apaf.backend.features.cartera_management.registrar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/cartera")
@RequiredArgsConstructor
@Validated
@Tag(name = "Listar Preview de Cartera", description = "Mostramos la tabla de preview de cartera ")
public class RegistrarCarteraController {
    private final RegistrarCarteraHandler handler;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Resgitra todos los datos de la cartera", description = "Tomando los datos base de cartera apartir del mes")
    public ResponseEntity<RegistrarCarteraResponse> registrar(
            @RequestParam String mesCorte,
            @Valid @RequestBody RegistrarCarteraCommand command) {

        YearMonth periodo = YearMonth.parse(mesCorte, DateTimeFormatter.ofPattern("yyyy-MM"));
        RegistrarCarteraResponse response = handler.handle(periodo, command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
