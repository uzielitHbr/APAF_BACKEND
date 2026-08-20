package app.apaf.backend.features.quarterly_analysis.branch_comparison;

import app.apaf.backend.features.quarterly_analysis.branch_comparison.AnalisisSucursalesResponse.*;
import app.apaf.backend.features.quarterly_analysis.domain.repository.CarteraAnaliticaReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ObtenerAnalisisSucursalHandler {

    private final CarteraAnaliticaReadRepository repository;

    public ObtenerAnalisisSucursalHandler(CarteraAnaliticaReadRepository repository) {
        this.repository = repository;
    }

    public AnalisisSucursalesResponse handle(ObtenerAnalisisSucursalQuery query) {
        LocalDate mesCorte = LocalDate.parse(query.fechaCorte() + "-01");
        
        List<CarteraAnaliticaReadRepository.ResumenCarteraProjection> resumen = repository.obtenerResumenPorSucursalYTipo(mesCorte);
        
        boolean isConsolidado = query.sucursal() == null || query.sucursal().equalsIgnoreCase("todas") || query.sucursal().isBlank();
        
        if (!isConsolidado) {
            resumen = resumen.stream()
                .filter(r -> query.sucursal().equalsIgnoreCase(r.getSucursalCodigo()))
                .toList();
        }
        
        Map<String, List<CarteraAnaliticaReadRepository.ResumenCarteraProjection>> byTipo = resumen.stream()
            .collect(Collectors.groupingBy(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getTipoCartera));

        BigDecimal globalTotal = resumen.stream()
            .map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCarteraTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SucursalDataDto> data = new ArrayList<>();
        int resumenCreditosVigentes = 0;
        BigDecimal resumenCapitalVigente = BigDecimal.ZERO;
        int resumenCreditosVencidos = 0;
        BigDecimal resumenCapitalVencido = BigDecimal.ZERO;
        int resumenCreditosTotal = 0;
        BigDecimal resumenCarteraTotal = BigDecimal.ZERO;

        for (Map.Entry<String, List<CarteraAnaliticaReadRepository.ResumenCarteraProjection>> entry : byTipo.entrySet()) {
            String tipoCartera = entry.getKey();
            int cVigentes = entry.getValue().stream().mapToInt(r -> r.getCreditosVigentes().intValue()).sum();
            BigDecimal capVigente = entry.getValue().stream().map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCapitalVigente).reduce(BigDecimal.ZERO, BigDecimal::add);
            int cVencidos = entry.getValue().stream().mapToInt(r -> r.getCreditosVencidos().intValue()).sum();
            BigDecimal capVencido = entry.getValue().stream().map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCapitalVencido).reduce(BigDecimal.ZERO, BigDecimal::add);
            int cTotal = entry.getValue().stream().mapToInt(r -> r.getCreditosTotal().intValue()).sum();
            BigDecimal carTotal = entry.getValue().stream().map(CarteraAnaliticaReadRepository.ResumenCarteraProjection::getCarteraTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal proporcion = globalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : carTotal.multiply(new BigDecimal("100")).divide(globalTotal, 2, RoundingMode.HALF_UP);
            
            data.add(new SucursalDataDto(
                tipoCartera,
                new CarteraDetalleDto(cVigentes, capVigente),
                new CarteraDetalleDto(cVencidos, capVencido),
                new CarteraTotalDto(cTotal, carTotal, proporcion)
            ));

            resumenCreditosVigentes += cVigentes;
            resumenCapitalVigente = resumenCapitalVigente.add(capVigente);
            resumenCreditosVencidos += cVencidos;
            resumenCapitalVencido = resumenCapitalVencido.add(capVencido);
            resumenCreditosTotal += cTotal;
            resumenCarteraTotal = resumenCarteraTotal.add(carTotal);
        }

        ResumenTotalDto resumenTotal = new ResumenTotalDto(
            new CarteraDetalleDto(resumenCreditosVigentes, resumenCapitalVigente),
            new CarteraDetalleDto(resumenCreditosVencidos, resumenCapitalVencido),
            new CarteraTotalDto(resumenCreditosTotal, resumenCarteraTotal, new BigDecimal("100.00"))
        );

        BigDecimal imor = resumenCarteraTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : resumenCapitalVencido.multiply(new BigDecimal("100")).divide(resumenCarteraTotal, 2, RoundingMode.HALF_UP);

        MetaDto meta = new MetaDto(
            query.fechaCorte(), "ANALISIS_SUCURSAL", isConsolidado ? "CONSOLIDADO" : query.sucursal()
        );
        KpisDto kpis = new KpisDto(imor);
        
        return new AnalisisSucursalesResponse(meta, kpis, data, resumenTotal);
    }
}
