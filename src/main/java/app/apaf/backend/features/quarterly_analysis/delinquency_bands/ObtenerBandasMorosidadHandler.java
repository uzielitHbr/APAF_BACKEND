package app.apaf.backend.features.quarterly_analysis.delinquency_bands;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.ClasificacionAnalisis;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.EstadoEjecucionTrimestral;
import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralEjecucion;
import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralBandaResultado;
import app.apaf.backend.features.quarterly_analysis.domain.repository.AnalisisTrimestralEjecucionRepository;
import app.apaf.backend.features.quarterly_analysis.domain.repository.AnalisisTrimestralBandaResultadoRepository;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ObtenerBandasMorosidadHandler {

    private final AnalisisTrimestralEjecucionRepository ejecucionRepo;
    private final AnalisisTrimestralBandaResultadoRepository bandaRepo;

    public ObtenerBandasMorosidadHandler(
            AnalisisTrimestralEjecucionRepository ejecucionRepo,
            AnalisisTrimestralBandaResultadoRepository bandaRepo) {
        this.ejecucionRepo = ejecucionRepo;
        this.bandaRepo = bandaRepo;
    }

    public BandasMorosidadResponse handle(ObtenerBandasMorosidadQuery query) {
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido.");
        }

        ClasificacionAnalisis clasificacion;
        try {
            clasificacion = ClasificacionAnalisis.valueOf(query.clasificacion().toUpperCase());
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.ClasificacionInvalidaException("Clasificación inválida.");
        }

        AnalisisTrimestralEjecucion ejecucion = ejecucionRepo
                .findByMesCorteAndEstado(mesCorte, EstadoEjecucionTrimestral.COMPLETADA)
                .orElseThrow(() -> new AnalisisTrimestralExceptions.EjecucionNoEncontradaException("No hay ejecución para este mes."));

        List<AnalisisTrimestralBandaResultado> resultados = bandaRepo.findByIdEjecucionAndClasificacion(ejecucion.getIdEjecucion(), clasificacion);

        Map<String, List<BandaSkeleton>> esqueleto = initSkeleton();

        for (AnalisisTrimestralBandaResultado b : resultados) {
            String tipo = b.getTipoCartera().name().toLowerCase();
            List<BandaSkeleton> bandas = esqueleto.get(tipo);
            if (bandas != null) {
                for (BandaSkeleton sk : bandas) {
                    if (sk.id.equals(b.getRangoId())) {
                        sk.creditos += b.getNumeroCreditos();
                        sk.monto = sk.monto.add(b.getImporteTotal());
                        break;
                    }
                }
            }
        }

        List<BandasMorosidadResponse.CategoriaBandaDto> categorias = new ArrayList<>();
        long globalCreditos = 0L;
        BigDecimal globalMonto = BigDecimal.ZERO;

        for (Map.Entry<String, List<BandaSkeleton>> entry : esqueleto.entrySet()) {
            String tipo = entry.getKey();
            long catCreditos = 0L;
            BigDecimal catMonto = BigDecimal.ZERO;
            List<BandasMorosidadResponse.DetalleBandaDto> detalles = new ArrayList<>();

            for (BandaSkeleton sk : entry.getValue()) {
                detalles.add(new BandasMorosidadResponse.DetalleBandaDto(
                        sk.id,
                        sk.etiqueta,
                        sk.creditos,
                        sk.monto
                ));
                catCreditos += sk.creditos;
                catMonto = catMonto.add(sk.monto);
            }

            BandasMorosidadResponse.TotalBandaDto catTotal = new BandasMorosidadResponse.TotalBandaDto(catCreditos, catMonto);
            categorias.add(new BandasMorosidadResponse.CategoriaBandaDto(tipo, detalles, catTotal));

            globalCreditos += catCreditos;
            globalMonto = globalMonto.add(catMonto);
        }

        BandasMorosidadResponse.TotalBandaDto resumenTotal = new BandasMorosidadResponse.TotalBandaDto(globalCreditos, globalMonto);
        return new BandasMorosidadResponse(query.fechaCorte(), query.clasificacion(), categorias, resumenTotal);
    }

    private static class BandaSkeleton {
        String id;
        String etiqueta;
        long creditos = 0L;
        BigDecimal monto = BigDecimal.ZERO;

        BandaSkeleton(String id, String etiqueta) {
            this.id = id;
            this.etiqueta = etiqueta;
        }
    }

    private Map<String, List<BandaSkeleton>> initSkeleton() {
        Map<String, List<BandaSkeleton>> map = new java.util.LinkedHashMap<>();
        
        List<BandaSkeleton> consumo = new ArrayList<>();
        consumo.add(new BandaSkeleton("01", "0 días"));
        consumo.add(new BandaSkeleton("02", "1-7 días"));
        consumo.add(new BandaSkeleton("03", "8-30 días"));
        consumo.add(new BandaSkeleton("04", "31-60 días"));
        consumo.add(new BandaSkeleton("05", "61-90 días"));
        consumo.add(new BandaSkeleton("06", "91-120 días"));
        consumo.add(new BandaSkeleton("07", "121- 180 días"));
        consumo.add(new BandaSkeleton("08", "181 ó más"));
        map.put("consumo", consumo);
        
        List<BandaSkeleton> comercial = new ArrayList<>();
        comercial.add(new BandaSkeleton("11", "0 días"));
        comercial.add(new BandaSkeleton("12", "1-30 días"));
        comercial.add(new BandaSkeleton("13", "31-60 días"));
        comercial.add(new BandaSkeleton("14", "61-90 días"));
        comercial.add(new BandaSkeleton("15", "91-120 días"));
        comercial.add(new BandaSkeleton("16", "121- 150 días"));
        comercial.add(new BandaSkeleton("17", "151 a 180 días"));
        comercial.add(new BandaSkeleton("18", "181 a 210 días"));
        comercial.add(new BandaSkeleton("19", "211 a 240 días"));
        comercial.add(new BandaSkeleton("110", "más de 240"));
        map.put("comercial", comercial);

        List<BandaSkeleton> vivienda = new ArrayList<>();
        vivienda.add(new BandaSkeleton("21", "0 días"));
        vivienda.add(new BandaSkeleton("22", "1-30 días"));
        vivienda.add(new BandaSkeleton("23", "31-60 días"));
        vivienda.add(new BandaSkeleton("24", "61-90 días"));
        vivienda.add(new BandaSkeleton("25", "91-120 días"));
        vivienda.add(new BandaSkeleton("26", "121- 150 días"));
        vivienda.add(new BandaSkeleton("27", "151 a 180 días"));
        vivienda.add(new BandaSkeleton("28", "181 a 1460 días"));
        vivienda.add(new BandaSkeleton("29", "más de 1460"));
        map.put("vivienda", vivienda);

        return map;
    }
}
