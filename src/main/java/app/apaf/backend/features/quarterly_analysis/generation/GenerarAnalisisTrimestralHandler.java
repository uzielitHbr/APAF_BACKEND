package app.apaf.backend.features.quarterly_analysis.generation;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.ClasificacionAnalisis;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.EstadoEjecucionTrimestral;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.TipoCarteraAnalitica;
import app.apaf.backend.features.quarterly_analysis.domain.model.*;
import app.apaf.backend.features.quarterly_analysis.domain.repository.*;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenerarAnalisisTrimestralHandler {

    private final AnalisisTrimestralEjecucionRepository ejecucionRepo;
    private final AnalisisTrimestralSucursalDetalleRepository detalleRepo;
    private final AnalisisTrimestralSucursalResumenRepository resumenRepo;
    private final AnalisisTrimestralProductoResultadoRepository productoRepo;
    private final AnalisisTrimestralBandaResultadoRepository bandaRepo;
    private final CarteraAnaliticaReadRepository readRepo;

    public GenerarAnalisisTrimestralHandler(
            AnalisisTrimestralEjecucionRepository ejecucionRepo,
            AnalisisTrimestralSucursalDetalleRepository detalleRepo,
            AnalisisTrimestralSucursalResumenRepository resumenRepo,
            AnalisisTrimestralProductoResultadoRepository productoRepo,
            AnalisisTrimestralBandaResultadoRepository bandaRepo,
            CarteraAnaliticaReadRepository readRepo) {
        this.ejecucionRepo = ejecucionRepo;
        this.detalleRepo = detalleRepo;
        this.resumenRepo = resumenRepo;
        this.productoRepo = productoRepo;
        this.bandaRepo = bandaRepo;
        this.readRepo = readRepo;
    }

    public void generarSiNoExiste(String fechaCorteStr) {
        if (fechaCorteStr == null || fechaCorteStr.isBlank()) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("El mes de corte es obligatorio.");
        }
        
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(fechaCorteStr + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido. Use yyyy-MM.");
        }

        Optional<AnalisisTrimestralEjecucion> existing = ejecucionRepo.findByMesCorteAndEstado(mesCorte, EstadoEjecucionTrimestral.COMPLETADA);
        if (existing.isPresent()) {
            return;
        }

        long registros = readRepo.contarRegistrosPorMesCorte(mesCorte);
        if (registros == 0) {
            throw new AnalisisTrimestralExceptions.PeriodoSinCarteraException("No se encontraron registros de cartera para el periodo solicitado.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null && auth.getName() != null) ? auth.getName() : "SISTEMA";

        UUID idEjecucion = UUID.randomUUID();
        AnalisisTrimestralEjecucion ejecucion = new AnalisisTrimestralEjecucion();
        ejecucion.setIdEjecucion(idEjecucion);
        ejecucion.setMesCorte(mesCorte);
        ejecucion.setFechaCorte(mesCorte);
        ejecucion.setNumeroVersion(1);
        ejecucion.setEstado(EstadoEjecucionTrimestral.INICIADA);
        ejecucion.setVersionFormula("v1.0");
        ejecucion.setTotalRegistros(0L);
        ejecucion.setActor(actor);
        ejecucion.setFechaInicio(LocalDateTime.now());
        ejecucion = ejecucionRepo.save(ejecucion);

        long totalRegistros = 0;

        // 1. Detalles y Resumen
        List<CarteraAnaliticaReadRepository.ResumenCarteraProjection> resumenProj = readRepo.obtenerResumenPorSucursalYTipo(mesCorte);
        BigDecimal globalTotal = resumenProj.stream()
                .map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCarteraTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<CarteraAnaliticaReadRepository.ResumenCarteraProjection>> bySucursal = resumenProj.stream()
                .filter(r -> r.getSucursalCodigo() != null)
                .collect(Collectors.groupingBy(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getSucursalCodigo));

        List<AnalisisTrimestralSucursalDetalle> detalles = new ArrayList<>();
        List<AnalisisTrimestralSucursalResumen> resumenes = new ArrayList<>();

        for (Map.Entry<String, List<CarteraAnaliticaReadRepository.ResumenCarteraProjection>> entry : bySucursal.entrySet()) {
            String sucursalCodigo = entry.getKey();
            List<CarteraAnaliticaReadRepository.ResumenCarteraProjection> sucursalData = entry.getValue();

            BigDecimal sucursalTotal = sucursalData.stream()
                    .map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCarteraTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sucursalVencida = sucursalData.stream()
                    .map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCapitalVencido)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (CarteraAnaliticaReadRepository.ResumenCarteraProjection r : sucursalData) {
                AnalisisTrimestralSucursalDetalle det = new AnalisisTrimestralSucursalDetalle();
                det.setIdEjecucion(idEjecucion);
                det.setSucursalCodigo(sucursalCodigo);
                det.setTipoCartera(TipoCarteraAnalitica.valueOf(r.getTipoCartera().toUpperCase()));
                det.setCreditosVigentes(r.getCreditosVigentes());
                det.setCapitalVigente(r.getCapitalVigente());
                det.setInteresesVigentes(r.getInteresesVigentes());
                det.setCarteraVigente(r.getCapitalVigente().add(r.getInteresesVigentes()));
                det.setCreditosVencidos(r.getCreditosVencidos());
                det.setCapitalVencido(r.getCapitalVencido());
                det.setInteresesVencidos(r.getInteresesVencidos());
                det.setCarteraVencida(r.getCapitalVencido().add(r.getInteresesVencidos()));
                det.setCreditosTotal(r.getCreditosTotal());
                det.setCarteraTotal(r.getCarteraTotal());

                BigDecimal propGlobal = globalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO 
                        : r.getCarteraTotal().multiply(new BigDecimal("100")).divide(globalTotal, 4, RoundingMode.HALF_UP);
                det.setProporcionGlobalPorcentaje(propGlobal);

                BigDecimal propSucursal = sucursalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO 
                        : r.getCarteraTotal().multiply(new BigDecimal("100")).divide(sucursalTotal, 4, RoundingMode.HALF_UP);
                det.setProporcionDentroSucursalPorcentaje(propSucursal);
                detalles.add(det);
            }

            AnalisisTrimestralSucursalResumen res = new AnalisisTrimestralSucursalResumen();
            res.setIdEjecucion(idEjecucion);
            res.setSucursalCodigo(sucursalCodigo);
            res.setCarteraTotal(sucursalTotal);
            res.setCarteraVencida(sucursalVencida);
            
            BigDecimal imor = sucursalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO 
                    : sucursalVencida.multiply(new BigDecimal("100")).divide(sucursalTotal, 4, RoundingMode.HALF_UP);
            res.setImorPorcentaje(imor);
            
            BigDecimal propGlobalSuc = globalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO 
                    : sucursalTotal.multiply(new BigDecimal("100")).divide(globalTotal, 4, RoundingMode.HALF_UP);
            res.setProporcionGlobal(propGlobalSuc);
            resumenes.add(res);
        }

        detalleRepo.saveAll(detalles);
        resumenRepo.saveAll(resumenes);
        totalRegistros += detalles.size() + resumenes.size();

        // 2. Productos
        List<CarteraAnaliticaReadRepository.ProductoVencidoProjection> productosProj = readRepo.obtenerProductosVencidos(mesCorte);
        BigDecimal totalVencido = productosProj.stream()
                .map(CarteraAnaliticaReadRepository.ProductoVencidoProjection::getImporteVencido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AnalisisTrimestralProductoResultado> productos = productosProj.stream().map(p -> {
            AnalisisTrimestralProductoResultado prod = new AnalisisTrimestralProductoResultado();
            prod.setIdEjecucion(idEjecucion);
            prod.setProductoCodigo(p.getProductoCodigo());
            prod.setProductoNombre(p.getProductoNombre());
            prod.setCreditosVencidos(p.getCreditosVencidos());
            prod.setImporteVencido(p.getImporteVencido());
            BigDecimal prop = totalVencido.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO 
                    : p.getImporteVencido().multiply(new BigDecimal("100")).divide(totalVencido, 4, RoundingMode.HALF_UP);
            prod.setProporcionPorcentaje(prop);
            return prod;
        }).toList();

        productoRepo.saveAll(productos);
        totalRegistros += productos.size();

        // 3. Bandas Morosidad
        List<CarteraAnaliticaReadRepository.BandaMorosidadProjection> bandasVencidaProj = readRepo.obtenerBandasMorosidadVencida(mesCorte);
        List<AnalisisTrimestralBandaResultado> bandas = new ArrayList<>();
        
        for (CarteraAnaliticaReadRepository.BandaMorosidadProjection b : bandasVencidaProj) {
            AnalisisTrimestralBandaResultado br = new AnalisisTrimestralBandaResultado();
            br.setIdEjecucion(idEjecucion);
            br.setClasificacion(ClasificacionAnalisis.VENCIDA);
            br.setTipoCartera(TipoCarteraAnalitica.valueOf(b.getTipoCartera().toUpperCase()));
            br.setRangoId(b.getRangoId() != null ? b.getRangoId() : "N/A");
            br.setRangoEtiqueta(b.getRangoEtiqueta() != null ? b.getRangoEtiqueta() : "Sin rango");
            br.setOrden(b.getOrden() != null ? b.getOrden() : 99);
            br.setNumeroCreditos(b.getCreditos());
            br.setImporteTotal(b.getImporte());
            bandas.add(br);
        }

        List<CarteraAnaliticaReadRepository.BandaMorosidadProjection> bandasTotalProj = readRepo.obtenerBandasMorosidadTotal(mesCorte);
        for (CarteraAnaliticaReadRepository.BandaMorosidadProjection b : bandasTotalProj) {
            AnalisisTrimestralBandaResultado br = new AnalisisTrimestralBandaResultado();
            br.setIdEjecucion(idEjecucion);
            br.setClasificacion(ClasificacionAnalisis.TOTAL);
            br.setTipoCartera(TipoCarteraAnalitica.valueOf(b.getTipoCartera().toUpperCase()));
            br.setRangoId(b.getRangoId() != null ? b.getRangoId() : "N/A");
            br.setRangoEtiqueta(b.getRangoEtiqueta() != null ? b.getRangoEtiqueta() : "Sin rango");
            br.setOrden(b.getOrden() != null ? b.getOrden() : 99);
            br.setNumeroCreditos(b.getCreditos());
            br.setImporteTotal(b.getImporte());
            bandas.add(br);
        }

        bandaRepo.saveAll(bandas);
        totalRegistros += bandas.size();

        // 4. Datos Grafica
        List<CarteraAnaliticaReadRepository.DatosGraficaProjection> graficaProj = readRepo.obtenerDatosGrafica(mesCorte);
        List<AnalisisTrimestralBandaResultado> graficaResultados = new ArrayList<>();
        for (CarteraAnaliticaReadRepository.DatosGraficaProjection g : graficaProj) {
            AnalisisTrimestralBandaResultado br = new AnalisisTrimestralBandaResultado();
            br.setIdEjecucion(idEjecucion);
            br.setClasificacion(ClasificacionAnalisis.GRAFICA);
            br.setTipoCartera(TipoCarteraAnalitica.CONSUMO); // Valor por defecto ya que agrupa todos
            br.setRangoId(g.getRango());
            br.setRangoEtiqueta("Grafica");
            br.setOrden(99);
            br.setNumeroCreditos(0L);
            br.setImporteTotal(g.getMonto());
            graficaResultados.add(br);
        }
        bandaRepo.saveAll(graficaResultados);
        totalRegistros += graficaResultados.size();

        ejecucion.setTotalRegistros(totalRegistros);
        ejecucion.setEstado(EstadoEjecucionTrimestral.COMPLETADA);
        ejecucion.setFechaFin(LocalDateTime.now());
        ejecucionRepo.save(ejecucion);
    }
}
