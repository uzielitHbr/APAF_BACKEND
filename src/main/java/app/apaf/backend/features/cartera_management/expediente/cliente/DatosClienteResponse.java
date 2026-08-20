package app.apaf.backend.features.cartera_management.expediente.cliente;

import java.time.LocalDate;

import app.apaf.backend.features.cartera_management.expediente.contrato.DatosContratoResponse;

public record DatosClienteResponse(
    LocalDate fechaNacimiento,
    Short edad,
    String genero,
    String ocupacion,
    String localidad,
    String estado,
    String municipio,
    DatosContratoResponse contrato
) {}
