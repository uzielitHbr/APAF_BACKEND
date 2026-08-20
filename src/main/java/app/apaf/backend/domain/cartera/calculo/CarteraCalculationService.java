package app.apaf.backend.domain.cartera.calculo;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CarteraCalculationService {

    private final MorosidadCalculator morosidadCalculator;
    private final ProyeccionCalculator proyeccionCalculator;
    private final SegmentacionCalculator segmentacionCalculator;
    private final RiesgoCalculator riesgoCalculator;
    private final ConcatenacionCalculator concatenacionCalculator;

    public CarteraDatosCalculados calcular(CarteraDatos base, LocalDate fechaCorteAnterior) {
        CarteraDatosCalculados resultado = new CarteraDatosCalculados();
        resultado.setCarteraDatos(base);
        
        // 1. Morosidad
        Short intervaloMorosidad = morosidadCalculator.calcularIntervalo(base.getTipoCarteraCalificacion(), base.getDiasMora());
        resultado.setIntervaloMorosidad(intervaloMorosidad);

        // 2. Proyeccion
        ProyeccionCalculator.ProyeccionResult proj = proyeccionCalculator.calcular(
                base.getPlazoCreditoMeses(), base.getMontoOriginal(), base.getCapitalVigente(), 
                base.getTasaOrdinariaNominalAnual(), base.getVigenteOVencido()
        );
        resultado.setAbonosRestantesMes1(proj.abonosRestantesMes1());
        resultado.setImporteCapitalProyectadoMes1(proj.importeCapitalProyectadoMes1());
        resultado.setInteresDevengadoProyectadoMes1(proj.interesDevengadoProyectadoMes1());
        resultado.setAbonosRestantesMes2(proj.abonosRestantesMes2());
        resultado.setImporteCapitalProyectadoMes2(proj.importeCapitalProyectadoMes2());
        resultado.setInteresDevengadoProyectadoMes2(proj.interesDevengadoProyectadoMes2());
        resultado.setAbonosRestantesMes3(proj.abonosRestantesMes3());
        resultado.setImporteCapitalProyectadoMes3(proj.importeCapitalProyectadoMes3());
        resultado.setInteresDevengadoProyectadoMes3(proj.interesDevengadoProyectadoMes3());

        // 3. Segmentacion
        resultado.setIntervaloEdad(segmentacionCalculator.calcularIntervaloEdad(base.getEdad()));
        Short plazoRemanente = segmentacionCalculator.calcularPlazoRemanente(fechaCorteAnterior, base.getFechaVencimiento());
        resultado.setPlazoRemanente(plazoRemanente);

        // 4. Riesgo
        Short cartRiesgoTraspaso = riesgoCalculator.calcularCartRiesgoTraspasoAVencida(base.getVigenteOVencido(), base.getDiasMora());
        resultado.setCartRiesgoTraspasoAVencida(cartRiesgoTraspaso);
        resultado.setDiasPorVencer(riesgoCalculator.calcularDiasPorVencer(fechaCorteAnterior, base.getFechaVencimiento()));
        resultado.setNivelDeRiesgoSic(null);
        resultado.setNivelDeRiesgoSicVencida(null);
        resultado.setNivelDeRiesgoSicGestionada(null);

        // 5. Concatenaciones & Derived
        Short carteraTipo = concatenacionCalculator.obtenerCarteraTipo(base.getTipoCarteraCalificacion());
        resultado.setCarteraTipo(carteraTipo);
        
        String tipoCarteraString = carteraTipo != null ? String.valueOf(carteraTipo) : null;
        String intervaloMorosidadString = intervaloMorosidad != null ? String.valueOf(intervaloMorosidad) : null;
        resultado.setIntervaloMorosidadYTipoCartera(concatenacionCalculator.concatenar(tipoCarteraString, intervaloMorosidadString));
        
        String numProducto = concatenacionCalculator.obtenerNumeroProducto(base.getNumeroContrato());
        resultado.setNumeroProducto(numProducto);
        resultado.setProductoGenerado(numProducto);
        
        resultado.setSucProdTasa(concatenacionCalculator.concatenar(base.getSucursal(), numProducto, 
                base.getTasaOrdinariaNominalAnual() != null ? base.getTasaOrdinariaNominalAnual().toString() : null));
                
        // Carteras Totales (Q + R + S + T)
        BigDecimal q = base.getCapitalVigente() != null ? base.getCapitalVigente() : BigDecimal.ZERO;
        BigDecimal r = base.getCapitalVencido() != null ? base.getCapitalVencido() : BigDecimal.ZERO;
        BigDecimal s = base.getIntDevNoCobradosVigentes() != null ? base.getIntDevNoCobradosVigentes() : BigDecimal.ZERO;
        BigDecimal t = base.getIntDevNoCobradosVencidos() != null ? base.getIntDevNoCobradosVencidos() : BigDecimal.ZERO;
        resultado.setCarteraTotal(q.add(r).add(s).add(t));
        
        resultado.setNumeroCreditos((short) (resultado.getCarteraTotal().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0));
        resultado.setContador(resultado.getNumeroCreditos());
        
        // Mappers
        resultado.setOcupacionAgrupada(OcupacionMapper.agrupar(base.getOcupacion()));
        String numEdo = EstadoMapper.mapearCodigo(base.getEstado());
        resultado.setNumeroEstadoMunicipio(numEdo != null && base.getMunicipio() != null ? numEdo + base.getMunicipio() : null);
        resultado.setEstadoMunicipio(concatenacionCalculator.concatenar(base.getEstado(), base.getMunicipio()));
        
        // BD / BE (recuperacion en el mes)
        if (base.getFechaUltimoPagoCapital() != null && base.getFechaUltimoPagoCapital().isAfter(fechaCorteAnterior)) {
            resultado.setRecuperacionEnElMesCapital(base.getMontoUltimoPagoCapital());
        } else {
            resultado.setRecuperacionEnElMesCapital(BigDecimal.ZERO);
        }
        
        if (base.getFechaUltimoPagoIntereses() != null && base.getFechaUltimoPagoIntereses().isAfter(fechaCorteAnterior)) {
            resultado.setRecuperacionEnElMesIntereses(base.getMontoUltimoPagoIntereses());
        } else {
            resultado.setRecuperacionEnElMesIntereses(BigDecimal.ZERO);
        }

        resultado.setCreditoPremierRequiereVerificacionDomiciliaria(
                "Premier".equalsIgnoreCase(base.getFinalidadCredito()) && 
                base.getMontoOriginal() != null && 
                base.getMontoOriginal().compareTo(BigDecimal.valueOf(50000)) > 0
        );
        
        Short conv = null;
        if (base.getFrecuenciaPagoCapital() != null) {
            String frec = base.getFrecuenciaPagoCapital().toLowerCase();
            if (frec.contains("mensual")) conv = 30;
            else if (frec.contains("quincenal")) conv = 15;
            else if (frec.contains("semanal")) conv = 7;
            else if (frec.contains("anual")) conv = 360;
            else if (frec.contains("semestral")) conv = 180;
            else if (frec.contains("tetramestral")) conv = 120;
            else if (frec.contains("trimestral")) conv = 90;
            else if (frec.contains("bimestral")) conv = 60;
        }
        resultado.setConvAbonosADias(conv);

        resultado.setProductoTipoCarteraEstatus(concatenacionCalculator.concatenar(numProducto, tipoCarteraString));

        String vigenteStr = "Vigente".equalsIgnoreCase(base.getVigenteOVencido()) ? "0" : 
                            ("Vencido".equalsIgnoreCase(base.getVigenteOVencido()) ? "1" : null);
        String contrato5 = base.getNumeroContrato() != null && base.getNumeroContrato().length() >= 5 
                            ? base.getNumeroContrato().substring(0, 5) : base.getNumeroContrato();
        String sucursalVigenteVencido = concatenacionCalculator.concatenar(contrato5, vigenteStr);
        resultado.setSucursalCreditoVigenteVencido(sucursalVigenteVencido);

        String socio5 = base.getNumeroSocio() != null && base.getNumeroSocio().length() >= 5 
                            ? base.getNumeroSocio().substring(0, 5) : base.getNumeroSocio();
        resultado.setOrigenSocio(socio5);
        resultado.setOrigenAuxiliar(contrato5);

        resultado.setNumeroCreditosCarteraVencida((short) ("Vencido".equalsIgnoreCase(base.getVigenteOVencido()) ? 1 : 0));
        
        // AW: tipoYEstatus
        String tipoYEstatus = concatenacionCalculator.concatenar(base.getTipoCarteraCalificacion(), base.getVigenteOVencido());
        resultado.setTipoYEstatus(tipoYEstatus);
        
        // AZ: intervaloDiasMorosidadYTipo
        resultado.setIntervaloDiasMorosidadYTipo(concatenacionCalculator.concatenar(tipoCarteraString, intervaloMorosidadString, base.getVigenteOVencido()));
        
        // CA: otorgadoMesRealizoMov
        // CB: accionSeguimiento
        // CQ: sucursalTipoCarteraEstatus
        resultado.setSucursalTipoCarteraEstatus(concatenacionCalculator.concatenar(contrato5, tipoYEstatus));
        
        // CD, CG, CH:
        // Omit CC when it is null using concatenacionCalculator
        String ccStr = cartRiesgoTraspaso != null ? String.valueOf(cartRiesgoTraspaso) : null;
        resultado.setOtorgadoMesMovRiesgoCarteraVencida(concatenacionCalculator.concatenar("CA_placeholder", ccStr));
        resultado.setOtorgadoMesRealizoMovSucursal(concatenacionCalculator.concatenar("CA_placeholder", contrato5));
        resultado.setCartRiesgoTraspasoVencidaSucursal(concatenacionCalculator.concatenar("CD_placeholder", contrato5, ccStr));
        resultado.setOtorgadoMesMovRiesgoVencidaSucursal(concatenacionCalculator.concatenar("CA_placeholder", "CG_placeholder"));
        
        String plazoRemStr = plazoRemanente != null ? String.valueOf(plazoRemanente) : null;
        resultado.setPlazoRemanenteSucursalVigenteVencido(concatenacionCalculator.concatenar(plazoRemStr, sucursalVigenteVencido));

        return resultado;
    }
}

