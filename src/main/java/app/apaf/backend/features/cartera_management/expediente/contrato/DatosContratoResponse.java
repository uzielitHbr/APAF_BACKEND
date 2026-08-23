package app.apaf.backend.features.cartera_management.expediente.contrato;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosContratoResponse(
        String numeroContrato,
        String sucursal,
        String clasificacionCredito,
        String productoCredito,
        String modalidadPago,
        LocalDate fechaOtorgamiento,
        BigDecimal montoOriginal,
        LocalDate fechaVencimiento,
        Integer plazoCreditoMeses,
        String renovadoReestructuradoNormal,
        String emproblemado,
        String tipoCarteraCalificacion,
        String finalidadCredito,
        String cce) {
}
