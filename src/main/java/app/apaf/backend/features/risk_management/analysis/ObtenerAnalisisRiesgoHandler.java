package app.apaf.backend.features.risk_management.analysis;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.RiskMetricsCalculator;
import app.apaf.backend.features.risk_management.domain.TipoLimite;
import app.apaf.backend.features.risk_management.domain.EstadoEvaluacionLimite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObtenerAnalisisRiesgoHandler {

    private final RiesgoAnalisisReadRepository readRepository;
    private final RiskMetricsCalculator calculator;

    public ObtenerAnalisisRiesgoHandler(RiesgoAnalisisReadRepository readRepository, RiskMetricsCalculator calculator) {
        this.readRepository = readRepository;
        this.calculator = calculator;
    }

    public RiesgoAnalisisResponse handle(String agrupacionStr, YearMonth mesCorte, int page, int size) {
        AgrupacionRiesgo agrupacion;
        try {
            agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException(
                    "Agrupación inválida");
        }

        LocalDate fechaCorte = mesCorte.atDay(1);
        TotalesGlobalesProjection totalesQuery = readRepository.obtenerTotalesGlobales(fechaCorte);

        Page<RiesgoSegmentoProjection> segmentosPage = readRepository.obtenerAnalisisPorAgrupacion(agrupacion,
                fechaCorte, PageRequest.of(page - 1, size));
        Page<RiesgoSegmentoProjection> todosSegmentos = readRepository.obtenerAnalisisPorAgrupacion(agrupacion,
                fechaCorte, org.springframework.data.domain.Pageable.unpaged());

        List<RiesgoAnalisisResponse.DatosSegmento> datosList = new ArrayList<>();

        for (RiesgoSegmentoProjection seg : segmentosPage.getContent()) {
            RiesgoAnalisisResponse.DatosSegmento dato = new RiesgoAnalisisResponse.DatosSegmento();
            
            String idLimite = seg.getIdLimite();
            if (idLimite == null) {
                idLimite = java.util.UUID.randomUUID().toString();
            }
            dato.setIdLimite(idLimite);
            
            dato.setClave(seg.getClave());
            
            String identificacion = seg.getIdentificacion();
            if (agrupacion == AgrupacionRiesgo.EDAD && identificacion != null) {
                switch (identificacion) {
                    case "1" -> identificacion = "18 A 25";
                    case "2" -> identificacion = "26 A 30";
                    case "3" -> identificacion = "31 A 35";
                    case "4" -> identificacion = "36 A 40";
                    case "5" -> identificacion = "41 A 45";
                    case "6" -> identificacion = "46 A 50";
                    case "7" -> identificacion = "51 A 55";
                    case "8" -> identificacion = "56 A 60";
                    case "9" -> identificacion = "61 A 65";
                    case "10" -> identificacion = "66 A 70";
                    case "11" -> identificacion = "71 A 75";
                    case "12" -> identificacion = "76 A 80";
                    case "13" -> identificacion = "81 A 85";
                    case "14" -> identificacion = "86 A 90";
                    default -> {
                        if (identificacion.matches("\\d+")) {
                            identificacion = "Otros rangos";
                        }
                    }
                }
            }
            dato.setIdentificacion(identificacion);
            dato.setNumeroCreditos(seg.getNumeroCreditos());
            dato.setCarteraVigente(seg.getCarteraVigente());
            dato.setCarteraVencida(seg.getCarteraVencida());
            dato.setCarteraTotal(seg.getCarteraTotal());

            BigDecimal concentracion = calculator.calcularConcentracionPorcentaje(seg.getCarteraTotal(),
                    totalesQuery.getCarteraTotal());
            dato.setConcentracionPorcentaje(concentracion);

            dato.setTipoLimite(seg.getTipoLimite());
            dato.setLimiteEstablecidoPorcentaje(seg.getLimiteEstablecidoPorcentaje());

            dato.setImorSegmentoPorcentaje(
                    calculator.calcularImorSegmentoPorcentaje(seg.getCarteraVencida(), seg.getCarteraTotal()));
            dato.setAportacionMorosidadPorcentaje(calculator
                    .calcularAportacionMorosidadPorcentaje(seg.getCarteraVencida(), totalesQuery.getCarteraTotal()));

            BigDecimal desviacion = calculator.calcularDesviacionPorcentaje(concentracion,
                    seg.getLimiteEstablecidoPorcentaje());
            dato.setDesviacionPorcentaje(desviacion);

            TipoLimite tipoLimite = seg.getTipoLimite() != null ? TipoLimite.valueOf(seg.getTipoLimite()) : null;
            dato.setExcesoPorcentaje(calculator.calcularExcesoPorcentaje(desviacion, tipoLimite));

            BigDecimal ihh = calculator.calcularIhh(concentracion);
            dato.setIhh(ihh);

            EstadoEvaluacionLimite estado = calculator.evaluarEstadoLimite(seg.getCarteraTotal(), concentracion,
                    seg.getLimiteEstablecidoPorcentaje(), tipoLimite);
            if (estado == EstadoEvaluacionLimite.DENTRO) {
                dato.setEstadoLimite("Dentro del parametro");
            } else if (estado == EstadoEvaluacionLimite.EXCEDIDO) {
                dato.setEstadoLimite("Fuera de los limites establecidos");
            } else {
                dato.setEstadoLimite("Sin límite configurado");
            }

            datosList.add(dato);
        }

        int dentro = 0, excedidos = 0, sinLimite = 0;
        BigDecimal ihhTotal = BigDecimal.ZERO;

        for (RiesgoSegmentoProjection s : todosSegmentos) {
            BigDecimal c = calculator.calcularConcentracionPorcentaje(s.getCarteraTotal(),
                    totalesQuery.getCarteraTotal());
            ihhTotal = ihhTotal.add(calculator.calcularIhh(c));

            TipoLimite tl = s.getTipoLimite() != null ? TipoLimite.valueOf(s.getTipoLimite()) : null;
            EstadoEvaluacionLimite est = calculator.evaluarEstadoLimite(s.getCarteraTotal(), c,
                    s.getLimiteEstablecidoPorcentaje(), tl);
            if (est == EstadoEvaluacionLimite.DENTRO)
                dentro++;
            else if (est == EstadoEvaluacionLimite.EXCEDIDO)
                excedidos++;
            else if (est == EstadoEvaluacionLimite.SIN_LIMITE)
                sinLimite++;
        }

        RiesgoAnalisisResponse.TotalesGlobales totales = new RiesgoAnalisisResponse.TotalesGlobales();
        totales.setNumeroCreditos(totalesQuery.getNumeroCreditos());
        totales.setCarteraVigente(totalesQuery.getCarteraVigente());
        totales.setCarteraVencida(totalesQuery.getCarteraVencida());
        totales.setCarteraTotal(totalesQuery.getCarteraTotal());
        totales.setConcentracionPorcentaje(new BigDecimal("100.0000"));
        totales.setImorTotalPorcentaje(calculator.calcularImorSegmentoPorcentaje(totalesQuery.getCarteraVencida(),
                totalesQuery.getCarteraTotal()));

        totales.setIhh(ihhTotal);
        if (ihhTotal.compareTo(new BigDecimal("0.10")) < 0)
            totales.setNivelConcentracion("Baja Concentración");
        else if (ihhTotal.compareTo(new BigDecimal("0.18")) < 0)
            totales.setNivelConcentracion("Moderada Concentración");
        else
            totales.setNivelConcentracion("Alta Concentración");

        totales.setSegmentosDentro(dentro);
        totales.setSegmentosExcedidos(excedidos);
        totales.setSegmentosSinLimite(sinLimite);

        RiesgoAnalisisResponse response = new RiesgoAnalisisResponse();
        response.setAgrupacion(agrupacionStr.toLowerCase());
        response.setMesCorte(mesCorte.toString());
        response.setFechaCorte(fechaCorte.toString());
        response.setDatos(datosList);
        response.setTotales(totales);

        RiesgoAnalisisResponse.Meta meta = new RiesgoAnalisisResponse.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setTotalElements(segmentosPage.getTotalElements());
        meta.setTotalPages(segmentosPage.getTotalPages());
        response.setMeta(meta);

        return response;
    }
}
