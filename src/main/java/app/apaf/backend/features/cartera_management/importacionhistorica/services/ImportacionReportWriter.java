package app.apaf.backend.features.cartera_management.importacionhistorica.services;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ImportacionReportWriter {

    public void escribirReporte(Path reportDirectory, YearMonth periodo, ReporteValidacionCsv reporte, ResultadoImportacionHistorica resultado) {
        if (reportDirectory == null) return;
        
        try {
            if (!Files.exists(reportDirectory)) {
                Files.createDirectories(reportDirectory);
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path reportFile = reportDirectory.resolve("reporte_cartera_" + periodo + "_" + timestamp + ".txt");
            
            try (BufferedWriter writer = Files.newBufferedWriter(reportFile, StandardCharsets.UTF_8)) {
                writer.write("========================================================\n");
                writer.write("REPORTE DE IMPORTACIÓN DE CARTERA - " + periodo + "\n");
                writer.write("========================================================\n\n");
                
                if (reporte != null) {
                    writer.write("RESUMEN DE VALIDACIÓN:\n");
                    writer.write("Errores bloqueantes: " + reporte.getErrores().size() + "\n");
                    writer.write("Advertencias: " + reporte.getAdvertencias().size() + "\n\n");
                    
                    if (!reporte.getErrores().isEmpty()) {
                        writer.write("--- ERRORES ---\n");
                        for (ErrorValidacionCsv err : reporte.getErrores()) {
                            writer.write(String.format("Línea %d | Campo: %s | %s\n", err.getLineNumber(), err.getField(), err.getMessage()));
                        }
                        writer.write("\n");
                    }
                    
                    if (!reporte.getAdvertencias().isEmpty()) {
                        writer.write("--- ADVERTENCIAS ---\n");
                        int maxAdvs = Math.min(100, reporte.getAdvertencias().size());
                        for (int i = 0; i < maxAdvs; i++) {
                            ErrorValidacionCsv adv = reporte.getAdvertencias().get(i);
                            writer.write(String.format("Línea %d | Campo: %s | %s\n", adv.getLineNumber(), adv.getField(), adv.getMessage()));
                        }
                        if (reporte.getAdvertencias().size() > 100) {
                            writer.write("... y " + (reporte.getAdvertencias().size() - 100) + " advertencias más omitidas por seguridad.\n");
                        }
                        writer.write("\n");
                    }
                }

                if (resultado != null) {
                    writer.write("--- RESULTADO DE PERSISTENCIA ---\n");
                    writer.write("Estado final: " + (resultado.exitoso() ? "EXITOSO" : "FALLIDO") + "\n");
                    writer.write("Mensaje: " + resultado.mensaje() + "\n");
                    writer.write("Total Filas Archivo: " + resultado.totalFilas() + "\n");
                    writer.write("Filas Insertadas: " + resultado.filasInsertadas() + "\n\n");
                }
                
                writer.write("========================================================\n");
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir el reporte de importación para " + periodo + ": " + e.getMessage());
        }
    }
    
    public void escribirErrorCritico(Path reportDirectory, YearMonth periodo, Exception ex) {
         if (reportDirectory == null) return;
         try {
             if (!Files.exists(reportDirectory)) {
                 Files.createDirectories(reportDirectory);
             }
             String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
             Path reportFile = reportDirectory.resolve("error_critico_cartera_" + periodo + "_" + timestamp + ".txt");
             
             try (BufferedWriter writer = Files.newBufferedWriter(reportFile, StandardCharsets.UTF_8)) {
                 writer.write("ERROR CRÍTICO EN IMPORTACIÓN - " + periodo + "\n\n");
                 writer.write("Clase: " + ex.getClass().getName() + "\n");
                 writer.write("Mensaje: " + ex.getMessage() + "\n");
             }
         } catch (IOException e) {
             System.err.println("No se pudo escribir el log de error crítico: " + e.getMessage());
         }
    }
}
