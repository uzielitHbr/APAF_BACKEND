package app.apaf.backend.features.cartera_management.registrar;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import app.apaf.backend.domain.cartera.exception.CarteraDomainException;
import app.apaf.backend.domain.cartera.repository.CarteraDatosCalculadosWriteRepository;
import app.apaf.backend.domain.cartera.repository.CarteraDatosWriteRepository;
import app.apaf.backend.domain.cartera.calculo.CarteraCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class RegistrarCarteraHandler {
    private final CarteraDatosWriteRepository datosRepository;
    private final CarteraDatosCalculadosWriteRepository calculadosRepository;
    private final CarteraCalculationService calculationService;

    @Transactional
    public RegistrarCarteraResponse handle(YearMonth periodo, RegistrarCarteraCommand command) {
        LocalDate mesCorte = periodo.atDay(1);
        LocalDate fechaCorte = periodo.atEndOfMonth();
        LocalDate corteAnterior = periodo.minusMonths(1).atEndOfMonth();

        if (datosRepository.existsByMesCorteAndNumeroContrato(mesCorte, command.numeroContrato())) {
            throw new CarteraDomainException("Contrato duplicado para el mes de corte");
        }

        CarteraDatos base = mapearBase(command, mesCorte, fechaCorte);
        datosRepository.saveAndFlush(base);

        CarteraDatosCalculados calculados = calculationService.calcular(base, corteAnterior);
        calculadosRepository.save(calculados);

        return new RegistrarCarteraResponse(base.getIdAnalisisMensual());
    }

    private CarteraDatos mapearBase(RegistrarCarteraCommand command, LocalDate mesCorte, LocalDate fechaCorte) {
        CarteraDatos base = new CarteraDatos();
        base.setMesCorte(mesCorte);
        base.setFechaCorte(fechaCorte);
        base.setNombreAcreditado(command.nombreAcreditado());
        base.setNumeroSocio(command.numeroSocio());
        base.setNumeroContrato(command.numeroContrato());
        base.setSucursal(command.sucursal());
        base.setClasificacionCredito(command.clasificacionCredito());
        base.setProductoCredito(command.productoCredito());
        base.setModalidadPago(command.modalidadPago());
        base.setFechaOtorgamiento(command.fechaOtorgamiento());
        base.setMontoOriginal(command.montoOriginal());
        base.setFechaVencimiento(command.fechaVencimiento());
        base.setTasaOrdinariaNominalAnual(command.tasaOrdinariaNominalAnual());
        base.setTasaMoratoriaNominalAnual(command.tasaMoratoriaNominalAnual());
        base.setPlazoCreditoMeses(command.plazoCreditoMeses());
        base.setFrecuenciaPagoCapital(command.frecuenciaPagoCapital());
        base.setFrecuenciaPagoIntereses(command.frecuenciaPagoIntereses());
        base.setDiasMora(command.diasMora());
        base.setCapitalVigente(command.capitalVigente());
        base.setCapitalVencido(command.capitalVencido());
        base.setIntDevNoCobradosVigentes(command.intDevNoCobradosVigentes());
        base.setIntDevNoCobradosVencidos(command.intDevNoCobradosVencidos());
        base.setIntDevNoCobradosCtasOrden(command.intDevNoCobradosCtasOrden());
        base.setFechaUltimoPagoCapital(command.fechaUltimoPagoCapital());
        base.setMontoUltimoPagoCapital(command.montoUltimoPagoCapital());
        base.setFechaUltimoPagoIntereses(command.fechaUltimoPagoIntereses());
        base.setMontoUltimoPagoIntereses(command.montoUltimoPagoIntereses());
        base.setRenovadoReestructuradoNormal(command.renovadoReestructuradoNormal());
        base.setEmproblemado(command.emproblemado());
        base.setVigenteOVencido(command.vigenteOVencido());
        base.setCargoAcreditadoParteRelacionada(command.cargoAcreditadoParteRelacionada());
        base.setMontoGarantiaLiquida(command.montoGarantiaLiquida());
        base.setCuentaGarantiaLiquida(command.cuentaGarantiaLiquida());
        base.setMontoGarantiaPrendaria(command.montoGarantiaPrendaria());
        base.setMontoGarantiaHipotecaria(command.montoGarantiaHipotecaria());
        base.setEprcContableParteCubierta(command.eprcContableParteCubierta());
        base.setEprcContableParteExpuesta(command.eprcContableParteExpuesta());
        base.setEprcContableXInteresesCee(command.eprcContableXInteresesCee());
        base.setImporteEstimacionAdicional(command.importeEstimacionAdicional());
        base.setLocalidad(command.localidad());
        base.setEstado(command.estado());
        base.setOcupacion(command.ocupacion());
        base.setMunicipio(command.municipio());
        base.setGenero(command.genero());
        base.setFechaNacimiento(command.fechaNacimiento());
        base.setEdad(command.edad());
        base.setTipoCarteraCalificacion(command.tipoCarteraCalificacion());
        base.setFinalidadCredito(command.finalidadCredito());
        base.setCce(command.cce());
        return base;
    }
}
