package app.apaf.backend.features.cartera_eprc.application.stratification;

import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEjecucionEntity;
import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEstratificacionDetalleEntity;
import app.apaf.backend.features.cartera_eprc.domain.entity.EprcResumenGlobalEntity;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcEjecucionRepository;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcEstratificacionDetalleRepository;
import app.apaf.backend.features.cartera_eprc.domain.repository.EprcResumenGlobalRepository;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ObtenerEstratificacionEprcHandler {

    private final EprcEjecucionRepository ejecucionRepo;
    private final EprcEstratificacionDetalleRepository detalleRepo;
    private final EprcResumenGlobalRepository resumenRepo;

    public ObtenerEstratificacionEprcHandler(
            EprcEjecucionRepository ejecucionRepo,
            EprcEstratificacionDetalleRepository detalleRepo,
            EprcResumenGlobalRepository resumenRepo) {
        this.ejecucionRepo = ejecucionRepo;
        this.detalleRepo = detalleRepo;
        this.resumenRepo = resumenRepo;
    }

    public EstratificacionCarteraResponse handle(ObtenerEstratificacionEprcQuery query) {
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido.");
        }

        EprcEjecucionEntity ejecucion = ejecucionRepo
                .findByMesCorteAndEstado(mesCorte, "COMPLETADA")
                .orElseThrow(() -> new AnalisisTrimestralExceptions.EjecucionNoEncontradaException("No hay ejecución para este mes."));

        List<EprcEstratificacionDetalleEntity> resultados = detalleRepo.findByIdEjecucion(ejecucion.getIdEjecucion());
        EprcResumenGlobalEntity resumen = resumenRepo.findByIdEjecucion(ejecucion.getIdEjecucion())
                .orElseThrow(() -> new AnalisisTrimestralExceptions.InconsistenciaDatosException("Resumen no encontrado"));

        SegmentoDetalleDto consumo = buildSegmento(resultados, "CONSUMO");
        SegmentoDetalleDto comercial = buildSegmento(resultados, "COMERCIAL");
        SegmentoDetalleDto vivienda = buildSegmento(resultados, "VIVIENDA");

        SegmentosEprcDto segmentos = new SegmentosEprcDto(consumo, comercial, vivienda);

        TotalesEprcDto sumaTotalGlobal = new TotalesEprcDto(
                "TOTAL GLOBAL",
                consumo.totales().numeroCreditos() + comercial.totales().numeroCreditos() + vivienda.totales().numeroCreditos(),
                consumo.totales().saldoCapital().add(comercial.totales().saldoCapital()).add(vivienda.totales().saldoCapital()),
                consumo.totales().saldoInteresVigente().add(comercial.totales().saldoInteresVigente()).add(vivienda.totales().saldoInteresVigente()),
                consumo.totales().saldoInteresVencido().add(comercial.totales().saldoInteresVencido()).add(vivienda.totales().saldoInteresVencido()),
                consumo.totales().saldoCarteraTotal().add(comercial.totales().saldoCarteraTotal()).add(vivienda.totales().saldoCarteraTotal()),
                consumo.totales().garantiaLiquida().add(comercial.totales().garantiaLiquida()).add(vivienda.totales().garantiaLiquida()),
                consumo.totales().garantiaHipotecaria().add(comercial.totales().garantiaHipotecaria()).add(vivienda.totales().garantiaHipotecaria()),
                consumo.totales().eprcParteCubierta().add(comercial.totales().eprcParteCubierta()).add(vivienda.totales().eprcParteCubierta()),
                consumo.totales().eprcParteExpuesta().add(comercial.totales().eprcParteExpuesta()).add(vivienda.totales().eprcParteExpuesta()),
                consumo.totales().estPrevInteresesVencidos().add(comercial.totales().estPrevInteresesVencidos()).add(vivienda.totales().estPrevInteresesVencidos()),
                consumo.totales().importeEstimacionPreventiva().add(comercial.totales().importeEstimacionPreventiva()).add(vivienda.totales().importeEstimacionPreventiva())
        );

        IndicadoresGlobalesDto indicadores = new IndicadoresGlobalesDto(
                resumen.getReservasRequeridas(),
                resumen.getCarteraTotalCuadro()
        );

        return new EstratificacionCarteraResponse(query.fechaCorte(), segmentos, sumaTotalGlobal, indicadores);
    }

    private SegmentoDetalleDto buildSegmento(List<EprcEstratificacionDetalleEntity> todos, String tipo) {
        List<EprcEstratificacionDetalleEntity> filtro = todos.stream()
                .filter(r -> r.getTipoCartera().equals(tipo))
                .collect(Collectors.toList());

        List<DetalleIntervaloEprcDto> detalle = filtro.stream().map(r -> new DetalleIntervaloEprcDto(
                r.getIntervaloVencimiento(),
                r.getNumeroCreditos(),
                r.getSaldoCapital(),
                r.getSaldoInteresVigente(),
                r.getSaldoInteresVencido(),
                r.getSaldoCarteraTotal(),
                r.getGarantiaLiquida(),
                r.getEprcParteCubierta(),
                r.getEprcParteExpuesta(),
                r.getEstPrevInteresesVencidos(),
                r.getImporteEstimacionPreventiva()
        )).collect(Collectors.toList());

        long creditos = 0L;
        BigDecimal capital = BigDecimal.ZERO;
        BigDecimal interesVigente = BigDecimal.ZERO;
        BigDecimal interesVencido = BigDecimal.ZERO;
        BigDecimal carteraTotal = BigDecimal.ZERO;
        BigDecimal garantiaLiquida = BigDecimal.ZERO;
        BigDecimal garantiaHipotecaria = BigDecimal.ZERO;
        BigDecimal eprcCubierta = BigDecimal.ZERO;
        BigDecimal eprcExpuesta = BigDecimal.ZERO;
        BigDecimal estPrevInt = BigDecimal.ZERO;
        BigDecimal importeEprc = BigDecimal.ZERO;

        for (EprcEstratificacionDetalleEntity r : filtro) {
            creditos += r.getNumeroCreditos();
            capital = capital.add(r.getSaldoCapital());
            interesVigente = interesVigente.add(r.getSaldoInteresVigente());
            interesVencido = interesVencido.add(r.getSaldoInteresVencido());
            carteraTotal = carteraTotal.add(r.getSaldoCarteraTotal());
            garantiaLiquida = garantiaLiquida.add(r.getGarantiaLiquida());
            garantiaHipotecaria = garantiaHipotecaria.add(r.getGarantiaHipotecaria());
            eprcCubierta = eprcCubierta.add(r.getEprcParteCubierta());
            eprcExpuesta = eprcExpuesta.add(r.getEprcParteExpuesta());
            estPrevInt = estPrevInt.add(r.getEstPrevInteresesVencidos());
            importeEprc = importeEprc.add(r.getImporteEstimacionPreventiva());
        }

        TotalesEprcDto totales = new TotalesEprcDto(
                "TOTAL " + tipo,
                creditos,
                capital,
                interesVigente,
                interesVencido,
                carteraTotal,
                garantiaLiquida,
                garantiaHipotecaria,
                eprcCubierta,
                eprcExpuesta,
                estPrevInt,
                importeEprc
        );

        return new SegmentoDetalleDto(detalle, totales);
    }
}
