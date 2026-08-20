package app.apaf.backend.features.quarterly_analysis.overdue_products;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.EstadoEjecucionTrimestral;
import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralEjecucion;
import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralProductoResultado;
import app.apaf.backend.features.quarterly_analysis.domain.repository.AnalisisTrimestralEjecucionRepository;
import app.apaf.backend.features.quarterly_analysis.domain.repository.AnalisisTrimestralProductoResultadoRepository;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerProductosVencidosHandler {

    private final AnalisisTrimestralEjecucionRepository ejecucionRepo;
    private final AnalisisTrimestralProductoResultadoRepository productoRepo;

    public ObtenerProductosVencidosHandler(
            AnalisisTrimestralEjecucionRepository ejecucionRepo,
            AnalisisTrimestralProductoResultadoRepository productoRepo) {
        this.ejecucionRepo = ejecucionRepo;
        this.productoRepo = productoRepo;
    }

    public ProductosVencidosResponse handle(ObtenerProductosVencidosQuery query) {
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido.");
        }

        AnalisisTrimestralEjecucion ejecucion = ejecucionRepo
                .findByMesCorteAndEstado(mesCorte, EstadoEjecucionTrimestral.COMPLETADA)
                .orElseThrow(() -> new AnalisisTrimestralExceptions.EjecucionNoEncontradaException("No hay ejecución para este mes."));

        List<AnalisisTrimestralProductoResultado> resultados = productoRepo.findByIdEjecucion(ejecucion.getIdEjecucion());

        long totalCreditos = 0L;
        BigDecimal totalMonto = BigDecimal.ZERO;

        List<ProductosVencidosResponse.ProductoVencidoDto> data = resultados.stream().map(r -> {
            return new ProductosVencidosResponse.ProductoVencidoDto(
                    r.getProductoCodigo(),
                    r.getProductoNombre(),
                    r.getCreditosVencidos(),
                    r.getImporteVencido()
            );
        }).toList();

        for (AnalisisTrimestralProductoResultado r : resultados) {
            totalCreditos += r.getCreditosVencidos();
            totalMonto = totalMonto.add(r.getImporteVencido());
        }

        ProductosVencidosResponse.TotalProductosDto resumenTotal = new ProductosVencidosResponse.TotalProductosDto(
                totalCreditos,
                totalMonto
        );

        return new ProductosVencidosResponse(query.fechaCorte(), data, resumenTotal);
    }
}
