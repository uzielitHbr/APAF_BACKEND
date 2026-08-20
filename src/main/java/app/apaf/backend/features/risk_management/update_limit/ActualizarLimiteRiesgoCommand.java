package app.apaf.backend.features.risk_management.update_limit;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ActualizarLimiteRiesgoCommand {
    private String tipoLimite;
    private BigDecimal limiteEstablecidoPorcentaje;
}
