package app.apaf.backend.features.risk_management.reactivate_limit;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ReactivarLimiteRiesgoCommand {
    private String tipoLimite;
    private BigDecimal limiteEstablecidoPorcentaje;
}
