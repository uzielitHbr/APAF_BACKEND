package app.apaf.backend.features.quarterly_analysis.domain.service;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.SeveridadInconsistencia;
import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralInconsistencia;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnalisisTrimestralReconciliationService {

    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");

    public List<AnalisisTrimestralInconsistencia> reconciliarBaseCalculados(UUID idEjecucion, Long registrosBase, Long registrosCalculados) {
        List<AnalisisTrimestralInconsistencia> inconsistencias = new ArrayList<>();
        if (!registrosBase.equals(registrosCalculados)) {
            inconsistencias.add(crearInconsistencia(
                    idEjecucion, "REC-001", SeveridadInconsistencia.CRITICA, "Integridad 1:1",
                    registrosBase.toString(), registrosCalculados.toString(),
                    "Filas base y calculadas no coinciden 1:1 en el periodo", true
            ));
        }
        return inconsistencias;
    }

    public List<AnalisisTrimestralInconsistencia> reconciliarTiposContraConsolidado(UUID idEjecucion, String periodo, BigDecimal sumaTipos, BigDecimal consolidado) {
        return validarTolerancia(idEjecucion, "REC-002", "Suma de Consumo+Comercial+Vivienda no cuadra con el consolidado en " + periodo, sumaTipos, consolidado, true);
    }

    public List<AnalisisTrimestralInconsistencia> reconciliarSucursalesContraConsolidado(UUID idEjecucion, String periodo, BigDecimal sumaSucursales, BigDecimal consolidado) {
        return validarTolerancia(idEjecucion, "REC-003", "Suma de sucursales no cuadra con el consolidado en " + periodo, sumaSucursales, consolidado, true);
    }

    public List<AnalisisTrimestralInconsistencia> reconciliarVigenteVencidaTotal(UUID idEjecucion, String contexto, BigDecimal carteraVigente, BigDecimal carteraVencida, BigDecimal carteraTotal) {
        BigDecimal vigente = carteraVigente != null ? carteraVigente : BigDecimal.ZERO;
        BigDecimal vencida = carteraVencida != null ? carteraVencida : BigDecimal.ZERO;
        BigDecimal suma = vigente.add(vencida);
        return validarTolerancia(idEjecucion, "REC-004", "Cartera Vigente + Vencida no cuadra con Cartera Total en " + contexto, suma, carteraTotal, true);
    }

    private List<AnalisisTrimestralInconsistencia> validarTolerancia(UUID idEjecucion, String codigo, String mensaje, BigDecimal esperado, BigDecimal obtenido, boolean bloqueante) {
        List<AnalisisTrimestralInconsistencia> inconsistencias = new ArrayList<>();
        BigDecimal esp = esperado != null ? esperado : BigDecimal.ZERO;
        BigDecimal obt = obtenido != null ? obtenido : BigDecimal.ZERO;

        if (esp.subtract(obt).abs().compareTo(TOLERANCIA) > 0) {
            inconsistencias.add(crearInconsistencia(
                    idEjecucion, codigo, bloqueante ? SeveridadInconsistencia.CRITICA : SeveridadInconsistencia.ERROR, "Reconciliacion",
                    esp.toPlainString(), obt.toPlainString(),
                    mensaje + ". Diferencia excede tolerancia de 0.01", bloqueante
            ));
        }
        return inconsistencias;
    }

    private AnalisisTrimestralInconsistencia crearInconsistencia(
            UUID idEjecucion, String codigo, SeveridadInconsistencia severidad, 
            String modulo, String valorEsperado, String valorObtenido, 
            String mensaje, boolean bloqueante) {
        AnalisisTrimestralInconsistencia inc = new AnalisisTrimestralInconsistencia();
        inc.setIdEjecucion(idEjecucion);
        inc.setCodigo(codigo);
        inc.setSeveridad(severidad);
        inc.setModulo(modulo);
        inc.setValorEsperado(valorEsperado);
        inc.setValorObtenido(valorObtenido);
        inc.setMensaje(mensaje);
        inc.setBloqueante(bloqueante);
        return inc;
    }
}
