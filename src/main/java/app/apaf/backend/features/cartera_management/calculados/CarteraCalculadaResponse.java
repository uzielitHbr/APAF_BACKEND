package app.apaf.backend.features.cartera_management.calculados;

import java.util.UUID;

import app.apaf.backend.features.cartera_management.calculados.proyecciones.ProyeccionesRecuperacionResponse;
import app.apaf.backend.features.cartera_management.calculados.riesgo.ClasificacionRiesgoResponse;
import app.apaf.backend.features.cartera_management.calculados.variables.VariablesControlResponse;

public record CarteraCalculadaResponse(
    UUID idAnalisisMensual,
    ProyeccionesRecuperacionResponse proyecciones,
    ClasificacionRiesgoResponse clasificacionRiesgo,
    VariablesControlResponse variablesControl
) {}
