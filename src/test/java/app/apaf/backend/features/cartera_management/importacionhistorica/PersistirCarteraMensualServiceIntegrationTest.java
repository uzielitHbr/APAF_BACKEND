package app.apaf.backend.features.cartera_management.importacionhistorica;

import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.apaf.backend.domain.cartera.calculo.CarteraCalculationService;
import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import app.apaf.backend.domain.cartera.repository.CarteraDatosCalculadosWriteRepository;
import app.apaf.backend.domain.cartera.repository.CarteraDatosWriteRepository;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.PersistenciaImportacionException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PersistirCarteraMensualServiceIntegrationTest {

    @Autowired
    private Environment env;

    @Autowired
    private PersistirCarteraMensualService persistirService;

    @Autowired
    private CarteraDatosWriteRepository baseRepository;

    @Autowired
    private CarteraDatosCalculadosWriteRepository calculadosRepository;

    @Autowired
    private CarteraImportacionHistoricaRepository auditoriaRepository;

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @MockBean
    private CarteraCsvMapper mapper;

    @MockBean
    private CarteraCalculationService calculationService;

    private UUID auditoriaId;

    @BeforeEach
    void setUp() {
        String dbUrl = env.getProperty("spring.datasource.url");
        assertFalse(dbUrl != null && dbUrl.contains("localhost:5432/apaf"), 
            "TEST ABORTED: The test is trying to connect to the dev database apaf. Check isolation.");
        
        baseRepository.deleteAll();
        calculadosRepository.deleteAll();
        auditoriaRepository.deleteAll();

        CarteraImportacionHistorica auditoria = new CarteraImportacionHistorica();
        auditoria.setMesCorte(YearMonth.of(2025, 12).atDay(1));
        auditoria.setFechaCorte(LocalDate.of(2025, 12, 31));
        auditoria.setFechaInicio(java.time.LocalDateTime.now());
        auditoria.setNombreArchivo("test.csv");
        auditoria.setHashSha256("fakehash");
        auditoria.setEstado("VALIDADA");
        auditoria.setEjecutadoPor("system");
        CarteraImportacionHistorica saved = auditoriaRepository.saveAndFlush(auditoria);
        auditoriaId = saved.getIdImportacion();
    }

    @Test
    void testPersistenciaLoteYMapsId() {
        ImportarCarteraHistoricaCommand command = new ImportarCarteraHistoricaCommand(
                YearMonth.of(2025, 12), null, null, 2);

        CarteraCsvRow row1 = new CarteraCsvRow(1, Arrays.asList("fake"));
        CarteraCsvRow row2 = new CarteraCsvRow(2, Arrays.asList("fake"));

        CarteraDatos base1 = crearCarteraDatosValida("C001", auditoriaId);
        CarteraDatos base2 = crearCarteraDatosValida("C002", auditoriaId);

        when(mapper.map(any(), any(), any()))
                .thenReturn(base1)
                .thenReturn(base2);

        CarteraDatosCalculados calc1 = new CarteraDatosCalculados();
        calc1.setCarteraDatos(base1);
        calc1.setCarteraTipo((short) 1);
        calc1.setCreditoPremierRequiereVerificacionDomiciliaria(false);

        CarteraDatosCalculados calc2 = new CarteraDatosCalculados();
        calc2.setCarteraDatos(base2);
        calc2.setCarteraTipo((short) 1);
        calc2.setCreditoPremierRequiereVerificacionDomiciliaria(false);

        when(calculationService.calcular(any(), any()))
                .thenReturn(calc1)
                .thenReturn(calc2);

        CarteraImportacionHistorica auditoria = auditoriaRepository.findById(auditoriaId).orElseThrow();

        ResultadoImportacionHistorica resultado = persistirService.persistir(
                command, Arrays.asList(row1, row2), new ReporteValidacionCsv(2), auditoria);

        assertThat(resultado.exitoso()).isTrue();
        assertThat(resultado.filasInsertadas()).isEqualTo(2);

        List<CarteraDatos> bases = baseRepository.findAll();
        assertThat(bases).hasSize(2);

        List<CarteraDatosCalculados> calculados = calculadosRepository.findAll();
        assertThat(calculados).hasSize(2);

        CarteraDatos savedBase1 = bases.stream().filter(b -> b.getNumeroContrato().equals("C001")).findFirst().orElseThrow();
        CarteraDatosCalculados savedCalc1 = calculados.stream().filter(c -> c.getIdAnalisisMensual().equals(savedBase1.getIdAnalisisMensual())).findFirst().orElseThrow();

        // 1 & 2. Confirmar que comparten el mismo UUID
        assertThat(savedBase1.getIdAnalisisMensual()).isEqualTo(savedCalc1.getIdAnalisisMensual());
        assertThat(savedCalc1.getCarteraDatos().getIdAnalisisMensual()).isEqualTo(savedBase1.getIdAnalisisMensual());
        
        // Assertions de id_importacion
        assertThat(savedBase1.getIdImportacion()).isNotNull();
        assertThat(savedBase1.getIdImportacion()).isEqualTo(auditoriaId);
        
        long basesConIdAuditoria = bases.stream().filter(b -> b.getIdImportacion().equals(auditoriaId)).count();
        assertThat(basesConIdAuditoria).isEqualTo(2); // Todas comparten el id
        assertThat(basesConIdAuditoria).isEqualTo(resultado.filasInsertadas());
    }

    @Test
    void testNoPuedeMarcarseCompletadaConCeroDatos() {
        ImportarCarteraHistoricaCommand command = new ImportarCarteraHistoricaCommand(
                YearMonth.of(2025, 12), null, null, 2);
        
        CarteraImportacionHistorica auditoria = auditoriaRepository.findById(auditoriaId).orElseThrow();

        assertThrows(PersistenciaImportacionException.class, () -> {
            persistirService.persistir(command, Arrays.asList(), new ReporteValidacionCsv(2), auditoria);
        });
        
        CarteraImportacionHistorica auditActualizada = auditoriaRepository.findById(auditoriaId).orElseThrow();
        assertThat(auditActualizada.getEstado()).isEqualTo("FALLIDA");
    }

    @Test
    void testFallaRevierteTodoElMesYAuditoriaPermanece() {
        ImportarCarteraHistoricaCommand command = new ImportarCarteraHistoricaCommand(
                YearMonth.of(2025, 12), null, null, 2);

        CarteraCsvRow row1 = new CarteraCsvRow(1, Arrays.asList("fake"));
        CarteraCsvRow row2 = new CarteraCsvRow(2, Arrays.asList("fake"));

        CarteraDatos base1 = crearCarteraDatosValida("C001", auditoriaId);
        
        when(mapper.map(any(), any(), any()))
                .thenReturn(base1)
                .thenThrow(new RuntimeException("Simulated error in second row mapping"));

        CarteraImportacionHistorica auditoria = auditoriaRepository.findById(auditoriaId).orElseThrow();

        assertThrows(PersistenciaImportacionException.class, () -> {
            persistirService.persistir(command, Arrays.asList(row1, row2), new ReporteValidacionCsv(2), auditoria);
        });

        // 5. Confirmar que una falla revierte todo el mes
        List<CarteraDatos> bases = baseRepository.findAll();
        assertThat(bases).isEmpty(); // Se revirtió row1 debido al error en row2

        // 6. Confirmar que la auditoría FALLIDA permanece
        CarteraImportacionHistorica auditActualizada = auditoriaRepository.findById(auditoriaId).orElseThrow();
        assertThat(auditActualizada.getEstado()).isEqualTo("FALLIDA");
    }

    private CarteraDatos crearCarteraDatosValida(String contrato, UUID idImportacion) {
        CarteraDatos base = new CarteraDatos();
        base.setMesCorte(LocalDate.of(2025, 12, 1));
        base.setFechaCorte(LocalDate.of(2025, 12, 31));
        base.setNumeroSocio("123");
        base.setNumeroContrato(contrato);
        base.setSucursal("001");
        base.setDiasMora(0);
        base.setCapitalVigente(BigDecimal.TEN);
        base.setCapitalVencido(BigDecimal.ZERO);
        base.setIntDevNoCobradosVigentes(BigDecimal.ZERO);
        base.setIntDevNoCobradosVencidos(BigDecimal.ZERO);
        base.setIntDevNoCobradosCtasOrden(BigDecimal.ZERO);
        base.setMontoUltimoPagoCapital(BigDecimal.ZERO);
        base.setMontoUltimoPagoIntereses(BigDecimal.ZERO);
        base.setEmproblemado(false);
        base.setMontoGarantiaLiquida(BigDecimal.ZERO);
        base.setMontoGarantiaPrendaria(BigDecimal.ZERO);
        base.setMontoGarantiaHipotecaria(BigDecimal.ZERO);
        base.setEprcContableParteCubierta(BigDecimal.ZERO);
        base.setEprcContableParteExpuesta(BigDecimal.ZERO);
        base.setEprcContableXInteresesCee(BigDecimal.ZERO);
        base.setImporteEstimacionAdicional(BigDecimal.ZERO);
        base.setIdImportacion(idImportacion);
        return base;
    }
}
