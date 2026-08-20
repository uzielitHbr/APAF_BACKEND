package app.apaf.backend.features.cartera_management.expediente;

import java.time.LocalDate;
import java.util.UUID;

import app.apaf.backend.features.cartera_management.expediente.cliente.DatosClienteResponse;
import app.apaf.backend.features.cartera_management.expediente.encabezado.EncabezadoResponse;
import app.apaf.backend.features.cartera_management.expediente.garantias.GarantiasEprcResponse;
import app.apaf.backend.features.cartera_management.expediente.saldos.SaldosPagosBaseResponse;

public record CarteraBaseResponse(
    UUID idAnalisisMensual,
    LocalDate mesCorte,
    LocalDate fechaCorte,
    EncabezadoResponse encabezado,
    DatosClienteResponse datosCliente,
    SaldosPagosBaseResponse saldosPagos,
    GarantiasEprcResponse garantiasEprc
) {}
