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

    private final RiesgoAnalisisReadRepository readRepository;

    public List<OpcionLimiteDto> handle(String agrupacionStr) {
        AgrupacionRiesgo agrupacion;
        try {
            agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RiesgoExceptions.ParametroInvalidoException("Agrupación inválida");
        }

        List<OpcionLimiteDto> opciones = readRepository.obtenerOpcionesDisponiblesPorAgrupacion(agrupacion);
        opciones.add(new OpcionLimiteDto("OTROS", "Otros"));
        
        return opciones;
    }
}
