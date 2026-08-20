package app.apaf.backend.features.cartera_eprc.application.snapshot;

import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEjecucionEntity;
import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEstratificacionDetalleEntity;
import app.apaf.backend.features.cartera_eprc.domain.entity.EprcResumenGlobalEntity;
import app.apaf.backend.features.cartera_eprc.domain.repository.CarteraEprcReadRepository;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcEjecucionRepository;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcEstratificacionDetalleRepository;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcResumenGlobalRepository;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerarSnapshotEprcHandler {

    private final EprcEjecucionRepository ejecucionRepo;
    private final EprcEstratificacionDetalleRepository detalleRepo;
    private final EprcResumenGlobalRepository resumenRepo;
    private final CarteraEprcReadRepository readRepo;

  

    public void generarSiNoExiste(YearMonth fechaCorteYm) {
        LocalDate mesCorte = fechaCorteYm.atDay(1);

        Optional<EprcEjecucionEntity> existing = ejecucionRepo.findByMesCorteAndEstado(mesCorte, "COMPLETADA");
        if (existing.isPresent()) {
            return;
        }

        long registros = readRepo.contarRegistrosPorMesCorte(mesCorte);
        if (registros == 0) {
            throw new AnalisisTrimestralExceptions.PeriodoSinCarteraException("No se encontraron registros de cartera para el periodo solicitado.");
        }

        EprcEjecucionEntity ejecucion = new EprcEjecucionEntity();
        ejecucion.setMesCorte(mesCorte);
        ejecucion.setEstado("COMPLETADA");
        ejecucion.setTotalRegistros(registros);
        ejecucion = ejecucionRepo.save(ejecucion);
                    

        List<CarteraEprcReadRepository.EstratificacionCarteraProjection> proyecciones = readRepo.agruparEstratificacionPorMesCorte(mesCorte);

        BigDecimal reservasTotales = BigDecimal.ZERO;
        BigDecimal carteraCuadroTotal = BigDecimal.ZERO;

        for (CarteraEprcReadRepository.EstratificacionCarteraProjection p : proyecciones) {
            EprcEstratificacionDetalleEntity det = new EprcEstratificacionDetalleEntity();
            det.setIdEjecucion(ejecucion.getIdEjecucion());
                
            det.setTipoCartera(p.getTipoCartera());
            det.setCodigoIntervalo(p.getCodigoIntervalo());
            det.setIntervaloVencimiento(p.getIntervaloVencimiento());
            det.setNumeroCreditos(p.getNumeroCreditos());
            det.setSaldoCapital(p.getSaldoCapital());
            det.setSaldoInteresVigente(p.getSaldoInteresVigente());
            det.setSaldoInteresVencido(p.getSaldoInteresVencido());
            det.setSaldoCarteraTotal(p.getSaldoCarteraTotal());
            det.setGarantiaLiquida(p.getGarantiaLiquida());
            det.setGarantiaHipotecaria(p.getGarantiaHipotecaria());
            det.setEprcParteCubierta(p.getEprcParteCubierta());
            det.setEprcParteExpuesta(p.getEprcParteExpuesta());
            det.setEstPrevInteresesVencidos(p.getEstPrevInteresesVencidos());
            det.setImporteEstimacionPreventiva(p.getImporteEstimacionPreventiva());

            detalleRepo.save(det);

            reservasTotales = reservasTotales.add(p.getImporteEstimacionPreventiva());
            carteraCuadroTotal = carteraCuadroTotal.add(p.getSaldoCarteraTotal());
        }

        EprcResumenGlobalEntity resumen = new EprcResumenGlobalEntity();
        resumen.setIdEjecucion(ejecucion.getIdEjecucion());
        resumen.setReservasRequeridas(reservasTotales);
        resumen.setCarteraTotalCuadro(carteraCuadroTotal);
        resumenRepo.save(resumen);
    }
}
