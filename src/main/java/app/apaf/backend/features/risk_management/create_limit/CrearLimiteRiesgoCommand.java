package app.apaf.backend.features.risk_management.create_limit;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class CrearLimiteRiesgoCommand {
    private String agrupacion;
    private String clave;
    private String identificacion;
    private String tipoLimite;
    private BigDecimal limiteEstablecidoPorcentaje;
}

