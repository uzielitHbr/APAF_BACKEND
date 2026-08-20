package app.apaf.backend.features.risk_management.list_limits;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ListarLimitesResponse {
    private String agrupacion;
    private List<LimiteDto> datos;
    private Meta meta;

    @Data
    public static class LimiteDto {
        private UUID idLimite;
        private String clave;
        private String identificacion;
        private String tipoLimite;
        private BigDecimal limiteEstablecidoPorcentaje;
        private Boolean activo;
        private Integer numeroVersion;
        private LocalDateTime vigenteDesde;
        private Long numCreditos;
    }

    @Data
    public static class Meta {
        private Long totalElements;
        private Integer page;
        private Integer size;
        private Integer totalPages;
    }
}
