package app.apaf.backend.features.risk_management.analysis;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RiesgoAnalisisResponse {
    @JsonProperty("agrupacionActual")
    private String agrupacion;
    private String mesCorte;
    private String fechaCorte;
    private List<DatosSegmento> datos;
    private TotalesGlobales totales;
    private Meta meta;

    @Data
    public static class DatosSegmento {
        private String idLimite;
        private String clave;
        private String identificacion;
        private Long numeroCreditos;
        private BigDecimal carteraVigente;
        private BigDecimal carteraVencida;
        private BigDecimal carteraTotal;
        private BigDecimal concentracionPorcentaje;
        private String tipoLimite;
        private BigDecimal limiteEstablecidoPorcentaje;
        @JsonProperty("imorPorProducto")
        private BigDecimal imorSegmentoPorcentaje;
        @JsonProperty("morosidadPorcentaje")
        private BigDecimal aportacionMorosidadPorcentaje;
        @JsonProperty("desviacion")
        private BigDecimal desviacionPorcentaje;
        private BigDecimal excesoPorcentaje;
        private BigDecimal ihh;
        @JsonProperty("comentario")
        private String estadoLimite;
    }

    @Data
    public static class TotalesGlobales {
        @JsonProperty("numCreditos")
        private Long numeroCreditos;
        private BigDecimal carteraVigente;
        private BigDecimal carteraVencida;
        private BigDecimal carteraTotal;
        private BigDecimal concentracionPorcentaje;
        @JsonProperty("morosidadPorcentaje")
        private BigDecimal imorTotalPorcentaje;
        private BigDecimal ihh;
        @JsonProperty("observacion")
        private String nivelConcentracion;
        private Integer segmentosDentro;
        private Integer segmentosExcedidos;
        private Integer segmentosSinLimite;
    }

    @Data
    public static class Meta {
        @JsonProperty("total")
        private Long totalElements;
        private Integer page;
        @JsonProperty("limit")
        private Integer size;
        private Integer totalPages;
    }
}
