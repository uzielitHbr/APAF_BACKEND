package app.apaf.backend.features.cartera_management.listar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@RestController
@RequestMapping("/api/v1/cartera")
@RequiredArgsConstructor
@Tag(name = "Listar Preview de Cartera", description = "Mostramos la tabla de preview de cartera ")
public class ListarCarteraController {

    private final ListarCarteraHandler handler;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar Preview de Cartera", description = "Mostramos la tabla de preview de cartera")
    public ResponseEntity<PaginatedResponse<CarteraPreviewResponse>> listar(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String mesCorte,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String producto) {
        return ResponseEntity.ok(handler.handle(page, size, mesCorte, searchTerm, sucursal, producto));
    }
}
