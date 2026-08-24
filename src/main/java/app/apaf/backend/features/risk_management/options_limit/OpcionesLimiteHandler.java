package app.apaf.backend.features.risk_management.options_limit;

import app.apaf.backend.features.risk_management.analysis.RiesgoAnalisisReadRepository;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpcionesLimiteHandler {

    public List<OpcionLimiteDto> handle() {
        return java.util.Arrays.stream(AgrupacionRiesgo.values())
                .map(agrupacion -> {
                    String identificacion = switch (agrupacion) {
                        case ESTADO -> "Estado";
                        case MUNICIPIO -> "Municipio";
                        case SUCURSAL -> "Sucursal";
                        case PRODUCTO -> "Producto";
                        case EDAD -> "Rango de Edad";
                        case GENERO -> "Género";
                        case OCUPACION -> "Ocupación";
                        case TIPO_CLASIFICACION -> "Tipo de Clasificación (Nuevo/Renovado)";
                        case ACREDITADO -> "Tipo de Acreditado";
                        case MODALIDAD -> "Modalidad de Pago";
                    };
                    return new OpcionLimiteDto(agrupacion.name(), identificacion);
                })
                .toList();
    }
}
