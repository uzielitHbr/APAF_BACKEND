package app.apaf.backend.features.cartera_management.importacionhistorica.controller;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.Charset;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/cartera/importaciones")
@RequiredArgsConstructor
@Validated
@Tag(name = "Importación de Cartera", description = "Regitrar datos sin procesar de cartera mensual")
public class CarteraImportacionController {

    private final ImportarCarteraHistoricaHandler handler;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar cartera mesnual", description = "Importa cartera cada mes  sin procesar por usuarios , Necesita mes de corte a ingresar para validar los datos no esten previamente registrados")
    public ResponseEntity<ResultadoImportacionHistorica> importarCartera(
            @RequestPart("file") MultipartFile file,
            @RequestParam("mesCorte") @PastOrPresent(message = "El mes de corte no puede estar en el futuro") @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesCorte,
            Authentication authentication) {

        String idUsuarioCreacion = authentication.getName();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("cartera_import_", ".csv");
            file.transferTo(tempFile.toFile());

            ImportarCarteraHistoricaCommand command = new ImportarCarteraHistoricaCommand(
                    mesCorte,
                    tempFile,
                    file.getOriginalFilename(),
                    Charset.forName("IBM850"),
                    500, // batch size default
                    idUsuarioCreacion);

            ResultadoImportacionHistorica resultado = handler.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo temporal", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    // Log error pero no interrumpir el flujo
                    System.err.println("No se pudo eliminar el archivo temporal: " + tempFile.toAbsolutePath());
                }
            }
        }
    }
}
