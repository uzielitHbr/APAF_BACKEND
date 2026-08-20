package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "apaf.cartera.importacion", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class CarteraImportacionHistoricaRunner implements CommandLineRunner {

    private final CarteraImportacionProperties properties;
    private final ImportarCarteraHistoricaHandler handler;
    private final CarteraCsvParser parser;
    private final CarteraCsvValidator validator;
    private final ImportacionHashService hashService;
    private final ImportacionReportWriter reportWriter;

    @Override
    public void run(String... args) throws Exception {
        ModoImportacion mode = properties.getMode() != null ? properties.getMode() : ModoImportacion.VALIDAR;

        if (properties.getSelectedPeriods() == null || properties.getSelectedPeriods().isEmpty()) {
            log.error("No se configuraron periodos para la importación histórica.");
            return;
        }

        if (properties.getFiles() == null || properties.getFiles().isEmpty()) {
            log.error("No se encontraron archivos configurados para importar.");
            return;
        }

        List<ArchivoCarteraConfig> configuredFiles = properties.getFiles().stream()
                .filter(f -> f.getPeriod() != null && f.getName() != null && !f.getName().trim().isEmpty())
                .toList();

        java.util.Set<java.time.YearMonth> parsedSelected = new java.util.LinkedHashSet<>();
        for (String sp : properties.getSelectedPeriods()) {
            if (sp == null || sp.trim().isEmpty()) continue;
            try {
                java.time.YearMonth ym = java.time.YearMonth.parse(sp.trim());
                if (!parsedSelected.add(ym)) {
                    log.error("El periodo {} está duplicado en la selección.", ym);
                    return;
                }
            } catch (Exception e) {
                log.error("El periodo seleccionado '{}' no tiene un formato válido (yyyy-MM).", sp);
                return;
            }
        }

        if (parsedSelected.isEmpty()) {
            log.error("No se configuraron periodos para la importación histórica.");
            return;
        }

        java.util.Set<java.time.YearMonth> availablePeriods = configuredFiles.stream()
                .map(ArchivoCarteraConfig::getPeriod)
                .collect(java.util.stream.Collectors.toSet());

        for (java.time.YearMonth selected : parsedSelected) {
            if (!availablePeriods.contains(selected)) {
                log.error("El periodo seleccionado {} no existe en la lista de archivos configurados.", selected);
                return;
            }
        }

        List<ArchivoCarteraConfig> filesToProcess = configuredFiles.stream()
                .filter(f -> parsedSelected.contains(f.getPeriod()))
                .sorted(Comparator.comparing(ArchivoCarteraConfig::getPeriod))
                .toList();

        log.info("Modo: {}", mode);
        log.info("Archivos configurados: {}", configuredFiles.size());
        
        String periodsStr = properties.getSelectedPeriods().stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(java.util.stream.Collectors.joining(", "));
        log.info("Periodos seleccionados: {}", periodsStr);
        log.info("Cantidad de archivos que se procesarán: {}", filesToProcess.size());

        if (properties.getDirectory() == null || properties.getDirectory().trim().isEmpty()) {
            log.error("El directorio de importación (apaf.cartera.importacion.directory) no está configurado.");
            return;
        }

        Path baseDir = Paths.get(properties.getDirectory());
        Path reportDir = properties.getReportDirectory() != null ? Paths.get(properties.getReportDirectory()) : null;
        Charset charset = properties.getDefaultCharset() != null ? Charset.forName(properties.getDefaultCharset()) : Charset.defaultCharset();
        
        boolean success = true;

        for (ArchivoCarteraConfig config : filesToProcess) {
            log.info("Procesando periodo: {}, Archivo: {}", config.getPeriod(), config.getName());
            
            try {
                Path archivo = baseDir.resolve(config.getName());
                ImportarCarteraHistoricaCommand command = new ImportarCarteraHistoricaCommand(
                        config.getPeriod(),
                        archivo,
                        charset,
                        properties.getBatchSize() > 0 ? properties.getBatchSize() : 250
                );

                if (mode == ModoImportacion.VALIDAR) {
                    log.info("Modo VALIDAR: ejecutando solo parseo y validación para {}", config.getPeriod());
                    String hash = hashService.calcularSha256(archivo);
                    List<CarteraCsvRow> filas = parser.parse(archivo, charset);
                    ReporteValidacionCsv reporte = validator.validar(command, filas, hash);
                    
                    reportWriter.escribirReporte(reportDir, config.getPeriod(), reporte, null);
                    reporte.throwIfInvalid();
                    
                    log.info("Periodo {} validado exitosamente sin errores bloqueantes.", config.getPeriod());
                } else {
                    log.info("Modo IMPORTAR: ejecutando flujo completo para {}", config.getPeriod());
                    ResultadoImportacionHistorica resultado = handler.handle(command);
                    
                    reportWriter.escribirReporte(reportDir, config.getPeriod(), null, resultado);
                    
                    log.info("Resultado {}: Insertadas {} / Total {}", 
                        resultado.exitoso() ? "EXITOSO" : "FALLIDO", 
                        resultado.filasInsertadas(), 
                        resultado.totalFilas()
                    );
                }

            } catch (Exception e) {
                log.error("Fallo crítico al procesar el periodo {}: {}", config.getPeriod(), e.getMessage(), e);
                reportWriter.escribirErrorCritico(reportDir, config.getPeriod(), e);
                success = false;
                
                if (properties.isStopOnError()) {
                    log.error("La propiedad stop-on-error es true. Deteniendo ejecución del runner.");
                    break;
                }
            }
        }
        
        if (!success) {
            log.error("La importación finalizó con errores.");
        } else {
            log.info("La importación finalizó correctamente.");
        }
    }
}
