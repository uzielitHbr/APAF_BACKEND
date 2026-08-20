package app.apaf.backend.features.cartera_management.calculados;

import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import app.apaf.backend.domain.cartera.exception.CarteraNoEncontradaException;
import app.apaf.backend.features.cartera_management.calculados.proyecciones.ProyeccionesRecuperacionResponse;
import app.apaf.backend.features.cartera_management.calculados.riesgo.ClasificacionRiesgoResponse;
import app.apaf.backend.features.cartera_management.calculados.variables.VariablesControlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObtenerCarteraCalculadaHandler {

        private final ObtenerCarteraCalculadaReadRepository repository;

        public List<CarteraCalculadaResponse> CarteraCalculadaHanlder(String numeroSocio, String mesCorteStr) {
                YearMonth periodo = YearMonth.parse(mesCorteStr, DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDate mesCorte = periodo.atDay(1);
                
                List<CarteraDatosCalculados> datos = repository.findByNumeroSocioAndMesCorte(numeroSocio, mesCorte);
                
                if (datos.isEmpty()) {
                        throw new CarteraNoEncontradaException(
                                        "Datos calculados no encontrados para el socio: " + numeroSocio + " en el mes: " + mesCorteStr);
                }

                return datos.stream().map(this::mapear).collect(Collectors.toList());
        }

        private CarteraCalculadaResponse mapear(CarteraDatosCalculados d) {
                ProyeccionesRecuperacionResponse proyecciones = new ProyeccionesRecuperacionResponse(
                                d.getCarteraTotal(), d.getRecuperacionEnElMesCapital(),
                                d.getRecuperacionEnElMesIntereses(),
                                d.getConvAbonosADias(), d.getAbonosRestantesMes1(), d.getImporteCapitalProyectadoMes1(),
                                d.getInteresDevengadoProyectadoMes1(), d.getAbonosRestantesMes2(),
                                d.getImporteCapitalProyectadoMes2(),
                                d.getInteresDevengadoProyectadoMes2(), d.getAbonosRestantesMes3(),
                                d.getImporteCapitalProyectadoMes3(),
                                d.getInteresDevengadoProyectadoMes3());

                ClasificacionRiesgoResponse riesgo = new ClasificacionRiesgoResponse(
                                d.getDiasPorVencer(), d.getIntervaloEdad(), d.getCartRiesgoTraspasoAVencida(),
                                d.getNivelDeRiesgoSic(), d.getNivelDeRiesgoSicVencida(),
                                d.getNivelDeRiesgoSicGestionada(),
                                d.getPlazoRemanente());

                VariablesControlResponse variables = new VariablesControlResponse(
                                d.getTipoYEstatus(), d.getCarteraTipo(), d.getProductoTipoCarteraEstatus(),
                                d.getIntervaloDiasMorosidadYTipo(), d.getIntervaloMorosidadYTipoCartera(),
                                d.getIntervaloMorosidad(), d.getContador(), d.getProductoGenerado(),
                                d.getNumeroProducto(), d.getNumeroCreditos(), d.getOcupacionAgrupada(),
                                d.getEstadoMunicipio(), d.getSucProdTasa(), d.getSucursalCreditoVigenteVencido(),
                                d.getOrigenSocio(), d.getOrigenAuxiliar(), d.getOtorgadoMesRealizoMov(),
                                d.getAccionSeguimiento(), d.getOtorgadoMesMovRiesgoCarteraVencida(),
                                d.getNumeroCreditosCarteraVencida(), d.getOtorgadoMesRealizoMovSucursal(),
                                d.getCartRiesgoTraspasoVencidaSucursal(), d.getOtorgadoMesMovRiesgoVencidaSucursal(),
                                d.getPlazoRemanenteSucursalVigenteVencido(), d.getNumeroEstadoMunicipio(),
                                d.getCreditoPremierRequiereVerificacionDomiciliaria(),
                                d.getSucursalTipoCarteraEstatus());

                return new CarteraCalculadaResponse(d.getIdAnalisisMensual(), proyecciones, riesgo, variables);
        }
}
