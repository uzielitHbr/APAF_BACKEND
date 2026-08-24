package app.apaf.backend.features.cartera_management.expediente;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.exception.CarteraNoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import app.apaf.backend.features.cartera_management.expediente.cliente.DatosClienteResponse;
import app.apaf.backend.features.cartera_management.expediente.encabezado.EncabezadoResponse;
import app.apaf.backend.features.cartera_management.expediente.garantias.GarantiasEprcResponse;
import app.apaf.backend.features.cartera_management.expediente.saldos.SaldosPagosBaseResponse;
import app.apaf.backend.features.cartera_management.expediente.contrato.DatosContratoResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObtenerCarteraBaseHandler {

        private final ObtenerCarteraBaseReadRepository repository;

        public List<CarteraBaseResponse> carteraHanlder(String numeroSocio, String mesCorteStr) {
                YearMonth periodo = YearMonth.parse(mesCorteStr, DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDate mesCorte = periodo.atDay(1);

                List<CarteraDatos> datos = repository.findByNumeroSocioAndMesCorte(numeroSocio, mesCorte);

                if (datos.isEmpty()) {
                        throw new CarteraNoEncontradaException(
                                        "Expediente base no encontrado para el socio: " + numeroSocio + " en el mes: "
                                                        + mesCorteStr);
                }

                return datos.stream().map(this::mapear).collect(Collectors.toList());
        }

        private CarteraBaseResponse mapear(CarteraDatos d) {
                EncabezadoResponse encabezado = new EncabezadoResponse(d.getNombreAcreditado(), d.getNumeroSocio());

                DatosContratoResponse contrato = new DatosContratoResponse(
                                d.getNumeroContrato(), d.getSucursal(), d.getClasificacionCredito(),
                                d.getProductoCredito(), d.getModalidadPago(), d.getFechaOtorgamiento(),
                                d.getMontoOriginal(), d.getFechaVencimiento(), d.getPlazoCreditoMeses(),
                                d.getRenovadoReestructuradoNormal(), d.getEmproblemado(),
                                d.getTipoCarteraCalificacion(),
                                d.getFinalidadCredito(), d.getCce());

                DatosClienteResponse cliente = new DatosClienteResponse(
                                d.getFechaNacimiento(), d.getEdad(), d.getGenero(), d.getOcupacion(),
                                d.getLocalidad(), d.getEstado(), d.getMunicipio(), contrato);

                SaldosPagosBaseResponse saldos = new SaldosPagosBaseResponse(
                                d.getDiasMora(), d.getVigenteOVencido(), d.getTasaOrdinariaNominalAnual(),
                                d.getTasaMoratoriaNominalAnual(), d.getCapitalVigente(), d.getCapitalVencido(),
                                d.getIntDevNoCobradosVigentes(), d.getIntDevNoCobradosVencidos(),
                                d.getIntDevNoCobradosCtasOrden(),
                                d.getFrecuenciaPagoCapital(), d.getFrecuenciaPagoIntereses(),
                                d.getFechaUltimoPagoCapital(),
                                d.getMontoUltimoPagoCapital(), d.getFechaUltimoPagoIntereses(),
                                d.getMontoUltimoPagoIntereses());

                GarantiasEprcResponse garantias = new GarantiasEprcResponse(
                                d.getCargoAcreditadoParteRelacionada(), d.getMontoGarantiaLiquida(),
                                d.getCuentaGarantiaLiquida(),
                                d.getMontoGarantiaPrendaria(), d.getMontoGarantiaHipotecaria(),
                                d.getEprcContableParteCubierta(),
                                d.getEprcContableParteExpuesta(), d.getEprcContableXInteresesCee(),
                                d.getImporteEstimacionAdicional());

                return new CarteraBaseResponse(
                                d.getIdAnalisisMensual(), d.getMesCorte(), d.getFechaCorte(),
                                encabezado, cliente, saldos, garantias);
        }
}
