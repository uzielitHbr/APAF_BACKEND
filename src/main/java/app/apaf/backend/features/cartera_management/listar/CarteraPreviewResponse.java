package app.apaf.backend.features.cartera_management.listar;

import java.math.BigDecimal;
import java.util.UUID;

public record CarteraPreviewResponse(
        UUID idAnalisisMensual,
        String nombreAcreditado,
        String numeroSocio,
        String numeroContrato,
        String sucursal,
        String productoCredito,
        BigDecimal capitalVigente) {
}
