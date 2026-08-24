package app.apaf.backend.features.risk_management.deactivate_limit;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
@Data
public class DesactivarLimiteRiesgoCommand {
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
