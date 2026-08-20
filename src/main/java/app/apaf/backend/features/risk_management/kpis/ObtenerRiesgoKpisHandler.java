package app.apaf.backend.features.risk_management.kpis;

import app.apaf.backend.features.risk_management.analysis.ObtenerAnalisisRiesgoHandler;
import app.apaf.backend.features.risk_management.analysis.RiesgoAnalisisResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

@Service
public class ObtenerRiesgoKpisHandler {

    private final ObtenerAnalisisRiesgoHandler analisisHandler;

    public ObtenerRiesgoKpisHandler(ObtenerAnalisisRiesgoHandler analisisHandler) {
        this.analisisHandler = analisisHandler;
    }

    public RiesgoKpisResponse handle(String agrupacionStr, YearMonth mesCorte) {
        RiesgoAnalisisResponse analisisResponse = analisisHandler.handle(agrupacionStr, mesCorte, 1, 1);
        
        RiesgoAnalisisResponse.TotalesGlobales totales = analisisResponse.getTotales();
        RiesgoAnalisisResponse.Meta meta = analisisResponse.getMeta();
        
        Integer totalSegmentos = meta.getTotalElements() != null ? meta.getTotalElements().intValue() : 0;
        Integer segmentosDentro = totales.getSegmentosDentro();
        Integer segmentosExcedidos = totales.getSegmentosExcedidos();
        Integer segmentosSinLimite = totales.getSegmentosSinLimite();
        Integer segmentosConLimite = segmentosDentro + segmentosExcedidos;
        
        BigDecimal porcentajeSegmentosExcedidos = BigDecimal.ZERO;
        if (segmentosConLimite > 0) {
            porcentajeSegmentosExcedidos = new BigDecimal(segmentosExcedidos)
                .divide(new BigDecimal(segmentosConLimite), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }

        RiesgoKpisResponse response = new RiesgoKpisResponse();
        RiesgoKpisResponse.Kpis kpis = new RiesgoKpisResponse.Kpis();

        // limite 1: Segmentos Analizados
        RiesgoKpisResponse.KpiCard limite1 = new RiesgoKpisResponse.KpiCard();
        limite1.setTitulo("Segmentos Analizados");
        limite1.setValor(String.valueOf(totalSegmentos));
        limite1.setSubtexto(totalSegmentos + " por " + agrupacionStr.toLowerCase());
        limite1.setTendencia("up");
        limite1.setPorcentaje("100%");
        kpis.setLimite1(limite1);

        // limite 2: IMOR Total
        RiesgoKpisResponse.KpiCard limite2 = new RiesgoKpisResponse.KpiCard();
        limite2.setTitulo("IMOR Total");
        limite2.setValor(totales.getImorTotalPorcentaje() + "%");
        limite2.setSubtexto("IMOR total " + totales.getImorTotalPorcentaje() + "%");
        limite2.setTendencia("down");
        limite2.setPorcentaje("0%");
        kpis.setLimite2(limite2);

        // limite 3: Estado de Límites
        RiesgoKpisResponse.KpiCard limite3 = new RiesgoKpisResponse.KpiCard();
        limite3.setTitulo("Estado de Límites");
        limite3.setValor(segmentosExcedidos + " Excedidos");
        limite3.setSubtexto("De " + segmentosConLimite + " limites configurados");
        limite3.setTendencia(segmentosExcedidos > 0 ? "down" : "up");
        limite3.setPorcentaje("0%");
        kpis.setLimite3(limite3);

        // limite 4: Índice Herfindahl-Hirschman
        RiesgoKpisResponse.KpiCard limite4 = new RiesgoKpisResponse.KpiCard();
        limite4.setTitulo("Índice Herfindahl-Hirschman");
        limite4.setValor(totales.getIhh().toString());
        limite4.setSubtexto(totales.getNivelConcentracion());
        limite4.setTendencia("up");
        limite4.setPorcentaje("0%");
        kpis.setLimite4(limite4);

        response.setKpis(kpis);
        response.setCarteraTotalConsolidada(totales.getCarteraTotal());

        return response;
    }
}
