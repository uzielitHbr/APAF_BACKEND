package app.apaf.backend.features.risk_management.options_limit;

import app.apaf.backend.domain.cartera.repository.CarteraDatosCalculadosWriteRepository;
import app.apaf.backend.domain.cartera.repository.CarteraDatosWriteRepository;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpcionesLimiteHandler {

    private final CarteraDatosWriteRepository repository;
    private final CarteraDatosCalculadosWriteRepository calculadosRepository;

    public List<OpcionLimiteDto> handle(String agrupacionStr) {
        AgrupacionRiesgo agrupacion;
        try {
            agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RiesgoExceptions.ParametroInvalidoException("Agrupación inválida");
        }

        List<OpcionLimiteDto> opciones = new ArrayList<>();

        switch (agrupacion) {
            case PRODUCTO:
                opciones.addAll(repository.findDistinctProductoCredito().stream()
                        .map(p -> new OpcionLimiteDto(p, p))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTROS PRODUCTOS", "Otros Productos"));
                break;
            case MUNICIPIO:
                opciones.addAll(repository.findDistinctMunicipio().stream()
                        .map(m -> new OpcionLimiteDto(m, m))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTROS MUNICIPIOS", "Otros Municipios"));
                break;
            case ESTADO:
                opciones.addAll(repository.findDistinctEstado().stream()
                        .map(e -> new OpcionLimiteDto(e, e))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTROS ESTADOS", "Otros Estados"));
                break;
            case EDAD:
                opciones.add(new OpcionLimiteDto("1", "Menor a 26 años"));
                opciones.add(new OpcionLimiteDto("2", "26 a 30 años"));
                opciones.add(new OpcionLimiteDto("3", "31 a 35 años"));
                opciones.add(new OpcionLimiteDto("4", "36 a 40 años"));
                opciones.add(new OpcionLimiteDto("5", "41 a 45 años"));
                opciones.add(new OpcionLimiteDto("6", "46 a 50 años"));
                opciones.add(new OpcionLimiteDto("7", "51 a 55 años"));
                opciones.add(new OpcionLimiteDto("8", "56 a 60 años"));
                opciones.add(new OpcionLimiteDto("9", "61 a 65 años"));
                opciones.add(new OpcionLimiteDto("10", "66 a 70 años"));
                opciones.add(new OpcionLimiteDto("11", "71 a 75 años"));
                opciones.add(new OpcionLimiteDto("12", "76 a 80 años"));
                opciones.add(new OpcionLimiteDto("13", "81 a 85 años"));
                opciones.add(new OpcionLimiteDto("14", "Mayor o igual a 86 años"));
                opciones.add(new OpcionLimiteDto("OTROS RANGOS", "Otros Rangos"));
                break;
            case GENERO:
                opciones.addAll(repository.findDistinctGenero().stream()
                        .map(g -> new OpcionLimiteDto(g, g))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTROS GENEROS", "Otros Géneros"));
                break;
            case SUCURSAL:
                opciones.addAll(repository.findDistinctSucursal().stream()
                        .map(s -> new OpcionLimiteDto(s, s))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTRAS SUCURSALES", "Otras Sucursales"));
                break;
            case MODALIDAD:
                opciones.addAll(repository.findDistinctModalidadPago().stream()
                        .map(m -> new OpcionLimiteDto(m, m))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTRAS MODALIDADES", "Otras Modalidades"));
                break;
            case ACREDITADO:
                opciones.add(new OpcionLimiteDto("OTROS ACREDITADOS", "Otros Acreditados"));
                break;
            case TIPO_CLASIFICACION:
                opciones.addAll(repository.findDistinctTipoClasificacion().stream()
                        .map(c -> new OpcionLimiteDto(c, c))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTRAS CLASIFICACIONES", "Otras Clasificaciones"));
                break;
            case OCUPACION:
                opciones.addAll(calculadosRepository.findDistinctOcupacionAgrupada().stream()
                        .map(o -> new OpcionLimiteDto(o, o))
                        .collect(Collectors.toList()));
                opciones.add(new OpcionLimiteDto("OTRAS OCUPACIONES", "Otras Ocupaciones"));
                break;
            default:
                opciones.add(new OpcionLimiteDto("OTROS", "Otros"));
        }

        return opciones;
    }
}
