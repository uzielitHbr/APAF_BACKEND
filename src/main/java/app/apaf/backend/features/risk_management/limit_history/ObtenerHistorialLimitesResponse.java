package app.apaf.backend.features.risk_management.limit_history;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ObtenerHistorialLimitesResponse {
    private List<HistorialDto> datos;
    private Meta meta;

    @Data
    public static class HistorialDto {
        private UUID idVersion;
        private UUID idLimite;
        private String accion;
        private String agrupacion;
        private String clave;
        private String identificacion;
        private String tipoLimite;
        private BigDecimal porcentajeAnterior;
        private BigDecimal nuevoPorcentaje;
        private Boolean activo;
        private LocalDateTime fechaModificacion;
        private ActorDto realizadoPor;
        private String motivo;
    }

    @Data
    public static class ActorDto {
        private Long idUsuario;
        private String nombre;
        private String correo;
    }

    @Data
    public static class Meta {
        private Long totalElements;
        private Integer page;
        private Integer size;
        private Integer totalPages;
    }
}
