package app.apaf.backend.features.risk_management.reactivate_limit;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
@Data
public class ReactivarLimiteRiesgoCommand {
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
