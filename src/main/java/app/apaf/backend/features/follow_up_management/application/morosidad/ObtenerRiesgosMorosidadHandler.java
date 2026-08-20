package app.apaf.backend.features.follow_up_management.application.morosidad;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoMorosidadEntity;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoMorosidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObtenerRiesgosMorosidadHandler {

    private final SeguimientoMorosidadRepository repository;

    public ObtenerRiesgosMorosidadHandler(SeguimientoMorosidadRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RiesgosMorosidadResponse handle(YearMonth mesCorte) {
        LocalDate fecha = mesCorte.atDay(1);
        List<SeguimientoMorosidadEntity> entidades = repository.findAll()
            .stream()
            .filter(e -> e.getMesCorte().equals(fecha))
            .collect(Collectors.toList());

        RiesgosMorosidadResponse.BloqueMoraDto mora61a89 = procesarBloque(entidades, "61a89");
        RiesgosMorosidadResponse.BloqueMoraDto mora30a60 = procesarBloque(entidades, "30a60");
        RiesgosMorosidadResponse.BloqueMoraDto mora1a29 = procesarBloque(entidades, "1a29");

        RiesgosMorosidadResponse.DataMorosidad data = new RiesgosMorosidadResponse.DataMorosidad(
            mora61a89, mora30a60, mora1a29
        );

        return new RiesgosMorosidadResponse(mesCorte.toString(), data);
    }

    private RiesgosMorosidadResponse.BloqueMoraDto procesarBloque(List<SeguimientoMorosidadEntity> entidades, String rango) {
        List<SeguimientoMorosidadEntity> filtrados = entidades.stream()
            .filter(e -> e.getRangoMora().equals(rango))
            .collect(Collectors.toList());

        List<RiesgosMorosidadResponse.DetalleMoraDto> detalle = filtrados.stream()
            .map(e -> new RiesgosMorosidadResponse.DetalleMoraDto(
                e.getSucursal(),
                e.getNumeroCreditos(),
                e.getCapitalVigente(),
                e.getInteresOrdVigente(),
                e.getCapitalVencido(),
                e.getInteresOrdVencido(),
                e.getCuentasOrden(),
                e.getSaldoTotal(),
                e.getCreditosConMovimiento(),
                e.getCreditosSinMovimiento(),
                e.getCreditosOtorgadosMes()
            ))
            .collect(Collectors.toList());

        RiesgosMorosidadResponse.TotalesMoraDto totales = new RiesgosMorosidadResponse.TotalesMoraDto(
            filtrados.stream().mapToInt(SeguimientoMorosidadEntity::getNumeroCreditos).sum(),
            filtrados.stream().map(SeguimientoMorosidadEntity::getCapitalVigente).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().map(SeguimientoMorosidadEntity::getInteresOrdVigente).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().map(SeguimientoMorosidadEntity::getCapitalVencido).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().map(SeguimientoMorosidadEntity::getInteresOrdVencido).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().map(SeguimientoMorosidadEntity::getCuentasOrden).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().map(SeguimientoMorosidadEntity::getSaldoTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
            filtrados.stream().mapToInt(SeguimientoMorosidadEntity::getCreditosConMovimiento).sum(),
            filtrados.stream().mapToInt(SeguimientoMorosidadEntity::getCreditosSinMovimiento).sum(),
            filtrados.stream().mapToInt(SeguimientoMorosidadEntity::getCreditosOtorgadosMes).sum()
        );

        return new RiesgosMorosidadResponse.BloqueMoraDto(detalle, totales);
    }
}
