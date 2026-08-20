package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CarteraTotalesProjection {
    LocalDate getFechaCorteMinima();
    LocalDate getFechaCorteMaxima();
    Long getTotalBase();
    Long getTotalCalculados();
    BigDecimal getTotalCartera();
    BigDecimal getTotalMontoOriginal();
    Long getTotalCreditosVigentes();
    Long getTotalCreditosVencidos();
    BigDecimal getCapitalVigente();
    BigDecimal getCapitalVencido();
    BigDecimal getInteresesVigentes();
    BigDecimal getInteresesVencidos();
    BigDecimal getInteresesOrden();
    BigDecimal getUltimosPagosCapital();
    BigDecimal getUltimosPagosInteres();
    Long getTotalDiasMora();
    BigDecimal getGarantiaLiquida();
    BigDecimal getEprcParteCubierta();
    BigDecimal getEprcParteExpuesta();
    BigDecimal getEprcInteresCee();
}
