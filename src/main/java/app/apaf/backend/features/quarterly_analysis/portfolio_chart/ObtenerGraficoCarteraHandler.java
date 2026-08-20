package app.apaf.backend.features.quarterly_analysis.portfolio_chart;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ObtenerGraficoCarteraHandler {

    private final AnalisisTrimestralEjecucionRepository ejecucionRepo;
    private final AnalisisTrimestralBandaResultadoRepository bandaRepo;

    public ObtenerGraficoCarteraHandler(
            AnalisisTrimestralEjecucionRepository ejecucionRepo,
            AnalisisTrimestralBandaResultadoRepository bandaRepo) {
        this.ejecucionRepo = ejecucionRepo;
        this.bandaRepo = bandaRepo;
    }

    public GraficoCarteraResponse handle(ObtenerGraficoCarteraQuery query) {
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido.");
        }

        AnalisisTrimestralEjecucion ejecucion = ejecucionRepo
                .findByMesCorteAndEstado(mesCorte, EstadoEjecucionTrimestral.COMPLETADA)
                .orElseThrow(() -> new AnalisisTrimestralExceptions.EjecucionNoEncontradaException("No hay ejecución para este mes."));

        // Se usa GRAFICA para obtener la tabla consolidada por dias_mora
        List<AnalisisTrimestralBandaResultado> resultados = bandaRepo
                .findByIdEjecucionAndClasificacion(ejecucion.getIdEjecucion(), ClasificacionAnalisis.GRAFICA);

        Map<String, BigDecimal> rangosSuma = new HashMap<>();
        rangosSuma.put("r-1", BigDecimal.ZERO);
        rangosSuma.put("r-2", BigDecimal.ZERO);
        rangosSuma.put("r-3", BigDecimal.ZERO);
        rangosSuma.put("r-4", BigDecimal.ZERO);
        rangosSuma.put("r-5", BigDecimal.ZERO);

        BigDecimal totalMonto = BigDecimal.ZERO;

        for (AnalisisTrimestralBandaResultado b : resultados) {
            String rangoId = b.getRangoId(); // Already mapped to r-1, r-2, etc. from SQL
            if (rangosSuma.containsKey(rangoId)) {
                rangosSuma.put(rangoId, rangosSuma.get(rangoId).add(b.getImporteTotal()));
            }
            totalMonto = totalMonto.add(b.getImporteTotal());
        }

        List<GraficoCarteraResponse.RangoGraficoDto> data = new ArrayList<>();
        data.add(new GraficoCarteraResponse.RangoGraficoDto("r-1", "0 - 30", rangosSuma.get("r-1")));
        data.add(new GraficoCarteraResponse.RangoGraficoDto("r-2", "31 - 60", rangosSuma.get("r-2")));
        data.add(new GraficoCarteraResponse.RangoGraficoDto("r-3", "61 - 90", rangosSuma.get("r-3")));
        data.add(new GraficoCarteraResponse.RangoGraficoDto("r-4", "91 - 120", rangosSuma.get("r-4")));
        data.add(new GraficoCarteraResponse.RangoGraficoDto("r-5", "121 En adelante", rangosSuma.get("r-5")));

        return new GraficoCarteraResponse(query.fechaCorte(), data, totalMonto);
    }
}
