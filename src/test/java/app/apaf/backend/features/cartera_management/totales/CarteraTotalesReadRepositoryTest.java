package app.apaf.backend.features.cartera_management.totales;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CarteraTotalesReadRepositoryTest {

    @Autowired
    private CarteraTotalesReadRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeObtenerTotalesCorrectamenteYMapearTipos() {
        LocalDate mesCorte = LocalDate.of(2026, 3, 1);
        
        CarteraDatos cd = new CarteraDatos();
        cd.setMesCorte(mesCorte);
        cd.setFechaCorte(LocalDate.of(2026, 3, 31));
        cd.setNombreAcreditado("Juan");
        cd.setNumeroSocio("S001");
        cd.setNumeroContrato("C001");
        cd.setSucursal("Suc1");
        cd.setMontoOriginal(new BigDecimal("1000.00"));
        cd.setVigenteOVencido("Vigente");
        cd.setCapitalVigente(new BigDecimal("800.00"));
        cd.setCapitalVencido(BigDecimal.ZERO);
        cd.setIntDevNoCobradosVigentes(new BigDecimal("50.00"));
        cd.setIntDevNoCobradosVencidos(BigDecimal.ZERO);
        cd.setIntDevNoCobradosCtasOrden(BigDecimal.ZERO);
        cd.setMontoUltimoPagoCapital(new BigDecimal("200.00"));
        cd.setMontoUltimoPagoIntereses(new BigDecimal("10.00"));
        cd.setDiasMora(0);
        cd.setMontoGarantiaLiquida(BigDecimal.ZERO);
        cd.setEprcContableParteCubierta(BigDecimal.ZERO);
        cd.setEprcContableParteExpuesta(new BigDecimal("20.00"));
        cd.setEprcContableXInteresesCee(BigDecimal.ZERO);
        cd.setEmproblemado(false);
        cd.setRenovadoReestructuradoNormal("NORMAL");
        cd.setClasificacionCredito("COMERCIAL");
        cd.setProductoCredito("CREDITO");
        cd.setModalidadPago("MENSUAL");
        cd.setTipoCarteraCalificacion("A");
        cd.setFinalidadCredito("CAPITAL");
        cd.setCce("NO");
        cd.setGenero("M");
        cd.setEstado("MOR");
        cd.setMunicipio("CUERNAVACA");
        cd.setLocalidad("CUERNAVACA");
        cd.setOcupacion("EMPLEADO");
        cd.setImporteEstimacionAdicional(BigDecimal.ZERO);
        cd.setMontoGarantiaHipotecaria(BigDecimal.ZERO);
        cd.setMontoGarantiaPrendaria(BigDecimal.ZERO);

        CarteraDatos savedCd = entityManager.persistAndFlush(cd);

        CarteraDatosCalculados cdc = new CarteraDatosCalculados();
        cdc.setCarteraDatos(savedCd);
        cdc.setCarteraTotal(new BigDecimal("850.00"));
        cdc.setCreditoPremierRequiereVerificacionDomiciliaria(false);

        entityManager.persistAndFlush(cdc);

        Optional<CarteraTotalesProjection> resultOpt = repository.obtenerTotalesPorMesCorte(mesCorte);

        assertThat(resultOpt).isPresent();
        CarteraTotalesProjection result = resultOpt.get();

        assertThat(result.getTotalBase()).isEqualTo(1L);
        assertThat(result.getTotalCalculados()).isEqualTo(1L);
        assertThat(result.getTotalCartera()).isEqualByComparingTo("850.00");
        assertThat(result.getTotalMontoOriginal()).isEqualByComparingTo("1000.00");
        assertThat(result.getTotalCreditosVigentes()).isEqualTo(1L);
        assertThat(result.getTotalCreditosVencidos()).isEqualTo(0L);
        assertThat(result.getCapitalVigente()).isEqualByComparingTo("800.00");
    }
}
