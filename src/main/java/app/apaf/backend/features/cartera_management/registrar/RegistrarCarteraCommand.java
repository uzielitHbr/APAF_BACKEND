package app.apaf.backend.features.cartera_management.registrar;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarCarteraCommand(
    String nombreAcreditado,
    @NotBlank String numeroSocio,
    @NotBlank String numeroContrato,
    @NotBlank String sucursal,
    String clasificacionCredito,
    String productoCredito,
    String modalidadPago,
    LocalDate fechaOtorgamiento,
    @PositiveOrZero BigDecimal montoOriginal,
    LocalDate fechaVencimiento,
    @PositiveOrZero BigDecimal tasaOrdinariaNominalAnual,
    @PositiveOrZero BigDecimal tasaMoratoriaNominalAnual,
    @PositiveOrZero Integer plazoCreditoMeses,
    String frecuenciaPagoCapital,
    String frecuenciaPagoIntereses,
    @NotNull @PositiveOrZero Integer diasMora,
    @NotNull @PositiveOrZero BigDecimal capitalVigente,
    @NotNull @PositiveOrZero BigDecimal capitalVencido,
    @NotNull @PositiveOrZero BigDecimal intDevNoCobradosVigentes,
    @NotNull @PositiveOrZero BigDecimal intDevNoCobradosVencidos,
    @NotNull @PositiveOrZero BigDecimal intDevNoCobradosCtasOrden,
    LocalDate fechaUltimoPagoCapital,
    @NotNull @PositiveOrZero BigDecimal montoUltimoPagoCapital,
    LocalDate fechaUltimoPagoIntereses,
    @NotNull @PositiveOrZero BigDecimal montoUltimoPagoIntereses,
    String renovadoReestructuradoNormal,
    @NotBlank String emproblemado,
    String vigenteOVencido,
    String cargoAcreditadoParteRelacionada,
    @NotNull @PositiveOrZero BigDecimal montoGarantiaLiquida,
    String cuentaGarantiaLiquida,
    @NotNull @PositiveOrZero BigDecimal montoGarantiaPrendaria,
    @NotNull @PositiveOrZero BigDecimal montoGarantiaHipotecaria,
    @NotNull @PositiveOrZero BigDecimal eprcContableParteCubierta,
    @NotNull @PositiveOrZero BigDecimal eprcContableParteExpuesta,
    @NotNull @PositiveOrZero BigDecimal eprcContableXInteresesCee,
    @NotNull @PositiveOrZero BigDecimal importeEstimacionAdicional,
    String localidad,
    String estado,
    String ocupacion,
    String municipio,
    String genero,
    LocalDate fechaNacimiento,
    @Min(0) @Max(130) Short edad,
    String tipoCarteraCalificacion,
    String finalidadCredito,
    String cce
) {}
