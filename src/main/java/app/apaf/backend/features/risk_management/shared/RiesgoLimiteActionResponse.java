package app.apaf.backend.features.risk_management.shared;

import lombok.Data;
import java.util.UUID;

@Data
public class RiesgoLimiteActionResponse {
    private UUID idLimite;
    private Integer numeroVersion;
    private String mensaje;
}
