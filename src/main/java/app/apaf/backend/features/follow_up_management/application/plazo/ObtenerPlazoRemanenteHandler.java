package app.apaf.backend.features.follow_up_management.application.plazo;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoPlazoEntity;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoPlazoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObtenerPlazoRemanenteHandler {

    private final SeguimientoPlazoRepository repository;

    public ObtenerPlazoRemanenteHandler(SeguimientoPlazoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PlazoRemanenteResponse handle(YearMonth mesCorte, String tipo) {
        LocalDate fecha = mesCorte.atDay(1);
        String vista = tipo == null || tipo.trim().isEmpty() ? "CONSOLIDADO" : tipo.toUpperCase();

        List<SeguimientoPlazoEntity> entidades = repository.findAll()
            .stream()
            .filter(e -> e.getMesCorte().equals(fecha))
            .filter(e -> e.getTipoVista().equals(vista))
            .collect(Collectors.toList());

        List<PlazoRemanenteResponse.PlazoDetalleDto> detalle = entidades.stream()
            .map(e -> new PlazoRemanenteResponse.PlazoDetalleDto(
                e.getPlazoRemanente(),
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
                e.getCreditosOtorgadosMes(),
                e.getImor(),
                e.getProporcion()
            ))
            .collect(Collectors.toList());

        PlazoRemanenteResponse.TotalesPlazoDto totales = new PlazoRemanenteResponse.TotalesPlazoDto(
            entidades.stream().mapToInt(SeguimientoPlazoEntity::getNumeroCreditos).sum(),
            entidades.stream().map(SeguimientoPlazoEntity::getCapitalVigente).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().map(SeguimientoPlazoEntity::getInteresOrdVigente).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().map(SeguimientoPlazoEntity::getCapitalVencido).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().map(SeguimientoPlazoEntity::getInteresOrdVencido).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().map(SeguimientoPlazoEntity::getCuentasOrden).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().map(SeguimientoPlazoEntity::getSaldoTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
            entidades.stream().mapToInt(SeguimientoPlazoEntity::getCreditosConMovimiento).sum(),
            entidades.stream().mapToInt(SeguimientoPlazoEntity::getCreditosSinMovimiento).sum(),
            entidades.stream().mapToInt(SeguimientoPlazoEntity::getCreditosOtorgadosMes).sum(),
            BigDecimal.ZERO, // Totales de IMOR general se podrian sumar o no, devolvemos 0
            new BigDecimal("100") // Proporcion total
        );

        return new PlazoRemanenteResponse(mesCorte.toString(), vista, detalle, totales);
    }
}
