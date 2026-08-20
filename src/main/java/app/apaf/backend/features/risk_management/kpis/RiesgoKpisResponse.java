package app.apaf.backend.features.risk_management.kpis;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RiesgoKpisResponse {
    private Kpis kpis;
    private BigDecimal carteraTotalConsolidada;

    @Data
    public static class Kpis {
        private KpiCard limite1;
        private KpiCard limite2;
        private KpiCard limite3;
        private KpiCard limite4;
    }

    @Data
    public static class KpiCard {
        private String titulo;
        private String valor;
        private String subtexto;
        private String tendencia;
        private String porcentaje;
    }
}
