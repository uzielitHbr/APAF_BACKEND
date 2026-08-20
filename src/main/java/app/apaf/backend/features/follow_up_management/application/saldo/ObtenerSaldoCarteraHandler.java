package app.apaf.backend.features.follow_up_management.application.saldo;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoSaldoEntity;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoSaldoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObtenerSaldoCarteraHandler {

    private final SeguimientoSaldoRepository repository;

    public ObtenerSaldoCarteraHandler(SeguimientoSaldoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SaldoCarteraResponse handle(YearMonth mesCorte) {
        LocalDate fecha = mesCorte.atDay(1);
        List<SeguimientoSaldoEntity> entidades = repository.findAll()
            .stream()
            .filter(e -> e.getMesCorte().equals(fecha))
            .collect(Collectors.toList());

        List<SaldoCarteraResponse.SucursalSaldoDto> sucursales = entidades.stream()
            .filter(e -> !e.getEsTotal())
            .map(e -> new SaldoCarteraResponse.SucursalSaldoDto(
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
                e.getImorSucursal(),
                e.getImorProyectado(),
                e.getProporcionCartera(),
                e.getImorGeneral()
            ))
            .collect(Collectors.toList());

        SeguimientoSaldoEntity totalEntity = entidades.stream()
            .filter(SeguimientoSaldoEntity::getEsTotal)
            .findFirst()
            .orElse(null);

        SaldoCarteraResponse.TotalesSaldoDto totales = null;
        if (totalEntity != null) {
            totales = new SaldoCarteraResponse.TotalesSaldoDto(
                totalEntity.getNumeroCreditos(),
                totalEntity.getCapitalVigente(),
                totalEntity.getInteresOrdVigente(),
                totalEntity.getCapitalVencido(),
                totalEntity.getInteresOrdVencido(),
                totalEntity.getCuentasOrden(),
                totalEntity.getSaldoTotal(),
                totalEntity.getCreditosConMovimiento(),
                totalEntity.getCreditosSinMovimiento(),
                totalEntity.getCreditosOtorgadosMes(),
                totalEntity.getImorSucursal(),
                totalEntity.getImorProyectado(),
                totalEntity.getProporcionCartera(),
                totalEntity.getImorGeneral()
            );
        }

        return new SaldoCarteraResponse(mesCorte.toString(), new SaldoCarteraResponse.DataSaldo(sucursales, totales));
    }
}
