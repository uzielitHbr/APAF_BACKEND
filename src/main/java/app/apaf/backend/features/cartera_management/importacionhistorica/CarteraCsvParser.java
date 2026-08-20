package app.apaf.backend.features.cartera_management.importacionhistorica;

import app.apaf.backend.features.cartera_management.importacionhistorica.exception.ArchivoCarteraNoEncontradoException;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.FormatoCsvInvalidoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CarteraCsvParser {

    public List<CarteraCsvRow> parse(Path file, Charset charset) {
        if (!Files.exists(file) || !Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new ArchivoCarteraNoEncontradoException("No se encontró o no se puede leer el archivo: " + file.toAbsolutePath());
        }

        List<CarteraCsvRow> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lineNumber++;
                    continue; // Saltar líneas vacías
                }
                
                String cleanLine = line;
                // Eliminar comillas externas si existen en la línea completa
                if (cleanLine.startsWith("\"") && cleanLine.endsWith("\"") && cleanLine.length() >= 2) {
                    cleanLine = cleanLine.substring(1, cleanLine.length() - 1);
                }

                String[] parts = cleanLine.split("\\|", -1); // Preservar columnas vacías al final
                
                if (parts.length != 47) {
                    throw new FormatoCsvInvalidoException(String.format("Error en archivo %s, línea %d: se esperaban 47 columnas pero se encontraron %d", file.getFileName().toString(), lineNumber, parts.length));
                }

                rows.add(CarteraCsvRow.builder()
                        .lineNumber(lineNumber)
                        .columns(Arrays.asList(parts))
                        .build());
                lineNumber++;
            }
        } catch (IOException e) {
             throw new FormatoCsvInvalidoException("Error de IO al leer el archivo: " + file.getFileName().toString() + ". Detalles: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new FormatoCsvInvalidoException("El archivo " + file.getFileName().toString() + " está vacío o no contiene filas válidas.");
        }

        return rows;
    }
}
