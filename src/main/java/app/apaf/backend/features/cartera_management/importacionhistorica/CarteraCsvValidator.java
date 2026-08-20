package app.apaf.backend.features.cartera_management.importacionhistorica;

import app.apaf.backend.features.cartera_management.importacionhistorica.exception.ContratoDuplicadoEnArchivoException;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.PeriodoCarteraYaImportadoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarteraCsvValidator {

    private final CsvValueParser valueParser;
    private final CarteraImportacionHistoricaRepository repository;

    public ReporteValidacionCsv validar(ImportarCarteraHistoricaCommand command, List<CarteraCsvRow> rows, String hash) {
        ReporteValidacionCsv reporte = new ReporteValidacionCsv(500);
        LocalDate mesCorte = command.periodo().atDay(1);
        
        // Validación de base de datos
        if (repository.findByMesCorteAndEstado(mesCorte, "COMPLETADA").isPresent()) {
            throw new PeriodoCarteraYaImportadoException("El periodo " + command.periodo() + " ya se encuentra importado como COMPLETADA.");
        }
        
        if (repository.findByHashSha256AndEstado(hash, "COMPLETADA").isPresent()) {
            throw new PeriodoCarteraYaImportadoException("El archivo con hash " + hash + " ya fue procesado y completado.");
        }

        Set<String> contratosSet = new HashSet<>();
        LocalDate fechaCorte = command.periodo().atEndOfMonth();

        for (CarteraCsvRow row : rows) {
            int ln = row.getLineNumber();
            
            // Obligatorios base
            String numeroSocio = row.getColumn(1);
            if (numeroSocio == null || numeroSocio.isBlank()) {
                reporte.addError(ln, "numeroSocio", "El número de socio no puede estar vacío");
            }
            
            String numeroContrato = row.getColumn(2);
            if (numeroContrato == null || numeroContrato.isBlank()) {
                reporte.addError(ln, "numeroContrato", "El número de contrato no puede estar vacío");
            } else {
                if (!contratosSet.add(numeroContrato)) {
                    throw new ContratoDuplicadoEnArchivoException(String.format("El contrato %s está duplicado en el archivo (línea %d)", 
                        numeroContrato.substring(0, Math.min(numeroContrato.length(), 4)) + "****", ln));
                }
            }
            
            String sucursal = row.getColumn(3);
            if (sucursal == null || sucursal.isBlank()) {
                reporte.addError(ln, "sucursal", "La sucursal no puede estar vacía");
            }

            // Fechas
            LocalDate fechaOtorgamiento = null;
            try {
                fechaOtorgamiento = valueParser.parseLocalDate(row.getColumn(7), "fechaOtorgamiento", ln);
                if (fechaOtorgamiento != null && fechaOtorgamiento.isAfter(fechaCorte)) {
                    reporte.addError(ln, "fechaOtorgamiento", "No debe ser posterior a la fecha de corte");
                }
            } catch (Exception e) { reporte.addError(ln, "fechaOtorgamiento", e.getMessage()); }

            try {
                LocalDate fechaUltimoPagoCap = valueParser.parseLocalDate(row.getColumn(21), "fechaUltimoPagoCapital", ln);
                if (fechaUltimoPagoCap != null && fechaUltimoPagoCap.isAfter(fechaCorte)) {
                    reporte.addError(ln, "fechaUltimoPagoCapital", "No debe ser posterior a la fecha de corte");
                }
            } catch (Exception e) { reporte.addError(ln, "fechaUltimoPagoCapital", e.getMessage()); }

            try {
                LocalDate fechaUltimoPagoInt = valueParser.parseLocalDate(row.getColumn(23), "fechaUltimoPagoIntereses", ln);
                if (fechaUltimoPagoInt != null && fechaUltimoPagoInt.isAfter(fechaCorte)) {
                    reporte.addError(ln, "fechaUltimoPagoIntereses", "No debe ser posterior a la fecha de corte");
                }
            } catch (Exception e) { reporte.addError(ln, "fechaUltimoPagoIntereses", e.getMessage()); }

            // Importes y enteros NOT NULL >= 0
            validarInteger(row, 15, "diasMora", ln, reporte);
            validarBigDecimal(row, 16, "capitalVigente", ln, reporte);
            validarBigDecimal(row, 17, "capitalVencido", ln, reporte);
            validarBigDecimal(row, 18, "intDevNoCobradosVigentes", ln, reporte);
            validarBigDecimal(row, 19, "intDevNoCobradosVencidos", ln, reporte);
            validarBigDecimal(row, 20, "intDevNoCobradosCtasOrden", ln, reporte);
            validarBigDecimal(row, 22, "montoUltimoPagoCapital", ln, reporte);
            validarBigDecimal(row, 24, "montoUltimoPagoIntereses", ln, reporte);
            
            validarBoolean(row, 26, "emproblemado", ln, reporte);
            
            validarBigDecimal(row, 29, "montoGarantiaLiquida", ln, reporte);
            validarBigDecimal(row, 31, "montoGarantiaPrendaria", ln, reporte);
            validarBigDecimal(row, 32, "montoGarantiaHipotecaria", ln, reporte);
            validarBigDecimal(row, 33, "eprcContableParteCubierta", ln, reporte);
            validarBigDecimal(row, 34, "eprcContableParteExpuesta", ln, reporte);
            validarBigDecimal(row, 35, "eprcContableXInteresesCee", ln, reporte);
            validarBigDecimal(row, 36, "importeEstimacionAdicional", ln, reporte);

            try {
                Short edad = valueParser.parseShort(row.getColumn(43), "edad", ln);
                if (edad != null && (edad < 0 || edad > 130)) {
                    reporte.addError(ln, "edad", "La edad debe estar entre 0 y 130");
                }
            } catch (Exception e) { reporte.addError(ln, "edad", e.getMessage()); }

            try {
                Integer plazoCreditoMeses = valueParser.parseInteger(row.getColumn(12), "plazoCreditoMeses", ln);
                if (plazoCreditoMeses != null && plazoCreditoMeses < 0) {
                    reporte.addError(ln, "plazoCreditoMeses", "El plazo debe ser mayor o igual a cero");
                }
            } catch (Exception e) { reporte.addError(ln, "plazoCreditoMeses", e.getMessage()); }

            // Advertencias
            String nombreAcreditado = row.getColumn(0);
            if (nombreAcreditado == null || nombreAcreditado.isBlank() || nombreAcreditado.contains("*")) {
                reporte.addAdvertencia(ln, "nombreAcreditado", "Nombre vacío o enmascarado");
            }
        }
        return reporte;
    }

    private void validarBigDecimal(CarteraCsvRow row, int index, String field, int ln, ReporteValidacionCsv reporte) {
        String val = row.getColumn(index);
        if (val == null || val.isBlank()) {
            reporte.addError(ln, field, "El valor es obligatorio y no puede estar vacío");
            return;
        }
        try {
            BigDecimal num = valueParser.parseBigDecimal(val, field, ln);
            if (num != null && num.compareTo(BigDecimal.ZERO) < 0) {
                reporte.addError(ln, field, "Debe ser mayor o igual a cero");
            }
        } catch (Exception e) {
            reporte.addError(ln, field, e.getMessage());
        }
    }

    private void validarInteger(CarteraCsvRow row, int index, String field, int ln, ReporteValidacionCsv reporte) {
        String val = row.getColumn(index);
        if (val == null || val.isBlank()) {
            reporte.addError(ln, field, "El valor es obligatorio y no puede estar vacío");
            return;
        }
        try {
            Integer num = valueParser.parseInteger(val, field, ln);
            if (num != null && num < 0) {
                reporte.addError(ln, field, "Debe ser mayor o igual a cero");
            }
        } catch (Exception e) {
            reporte.addError(ln, field, e.getMessage());
        }
    }
    
    private void validarBoolean(CarteraCsvRow row, int index, String field, int ln, ReporteValidacionCsv reporte) {
        String val = row.getColumn(index);
        if (val == null || val.isBlank()) {
            reporte.addError(ln, field, "El valor es obligatorio y no puede estar vacío");
            return;
        }
        try {
            valueParser.parseBoolean(val, field, ln);
        } catch (Exception e) {
            reporte.addError(ln, field, e.getMessage());
        }
    }
}
