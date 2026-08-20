package app.apaf.backend.features.quarterly_analysis.branch_proportions;

import app.apaf.backend.features.quarterly_analysis.branch_proportions.ProporcionesResponse.*;
import app.apaf.backend.features.quarterly_analysis.domain.repository.CarteraAnaliticaReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ObtenerProporcionesSucursalHandler {

    private final CarteraAnaliticaReadRepository repository;

    public ObtenerProporcionesSucursalHandler(CarteraAnaliticaReadRepository repository) {
        this.repository = repository;
    }

    public ProporcionesResponse handle(ObtenerProporcionesSucursalQuery query) {
        LocalDate mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        
        List<CarteraAnaliticaReadRepository.ProporcionSucursalProjection> proporciones = repository.obtenerProporcionesPorSucursal(mesCorte);
        
        BigDecimal globalTotal = proporciones.stream()
            .map(CarteraAnaliticaReadRepository.ProporcionSucursalProjection::getCarteraTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        List<ProporcionDataDto> data = proporciones.stream()
            .map(p -> {
                BigDecimal porcentaje = globalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : p.getCarteraTotal().multiply(new BigDecimal("100")).divide(globalTotal, 2, RoundingMode.HALF_UP);
                return new ProporcionDataDto(p.getSucursalId(), p.getNombre(), porcentaje);
            })
            .collect(Collectors.toList());

        MetaProporcionDto meta = new MetaProporcionDto(query.fechaCorte(), "PROPORCIONES_SUCURSAL");
        
        return new ProporcionesResponse(meta, data);
    }
}
