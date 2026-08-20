package app.apaf.backend.features.cartera_management.importacionhistorica;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CarteraImportacionHistoricaRunnerTest {

    @Mock
    private CarteraImportacionProperties properties;
    @Mock
    private ImportarCarteraHistoricaHandler handler;
    @Mock
    private CarteraCsvParser parser;
    @Mock
    private CarteraCsvValidator validator;
    @Mock
    private ImportacionHashService hashService;
    @Mock
    private ImportacionReportWriter reportWriter;

    @InjectMocks
    private CarteraImportacionHistoricaRunner runner;

    private List<ArchivoCarteraConfig> allFiles;

    @BeforeEach
    void setUp() {
        allFiles = new ArrayList<>();
        allFiles.add(crearConfig(YearMonth.of(2026, 3), "marzo.csv"));
        allFiles.add(crearConfig(YearMonth.of(2025, 12), "diciembre.csv"));
        allFiles.add(crearConfig(YearMonth.of(2026, 1), "enero.csv"));
        allFiles.add(crearConfig(YearMonth.of(2026, 2), "febrero.csv"));
    }

    private ArchivoCarteraConfig crearConfig(YearMonth period, String name) {
        ArchivoCarteraConfig config = new ArchivoCarteraConfig();
        config.setPeriod(period);
        config.setName(name);
        return config;
    }

    @Test
    void testSoloProcesanPeriodosSeleccionadosYNuncaLleganAlParserLosNoSeleccionados(CapturedOutput output) throws Exception {
        when(properties.getMode()).thenReturn(ModoImportacion.VALIDAR);
        when(properties.getSelectedPeriods()).thenReturn(Arrays.asList("2025-12", "2026-02"));
        when(properties.getFiles()).thenReturn(allFiles);
        when(properties.getDirectory()).thenReturn("/tmp/carteras");
        when(properties.getDefaultCharset()).thenReturn("UTF-8");
        
        when(hashService.calcularSha256(any(Path.class))).thenReturn("fakehash");
        when(parser.parse(any(Path.class), any(Charset.class))).thenReturn(Collections.emptyList());
        when(validator.validar(any(), any(), any())).thenReturn(new ReporteValidacionCsv(100));

        runner.run();

        // Verificar que parser solo se llamó 2 veces (Diciembre y Febrero)
        verify(parser, times(2)).parse(any(Path.class), any(Charset.class));
        verify(handler, never()).handle(any()); // Validar no invoca persistencia
        
        // Enero y Marzo nunca llegan al parser
        String logs = output.getOut();
        org.junit.jupiter.api.Assertions.assertTrue(logs.contains("Procesando periodo: 2025-12"));
        org.junit.jupiter.api.Assertions.assertTrue(logs.contains("Procesando periodo: 2026-02"));
        org.junit.jupiter.api.Assertions.assertFalse(logs.contains("Procesando periodo: 2026-01"));
        org.junit.jupiter.api.Assertions.assertFalse(logs.contains("Procesando periodo: 2026-03"));
    }

    @Test
    void testListaVaciaNoProcesaArchivos(CapturedOutput output) throws Exception {
        when(properties.getMode()).thenReturn(ModoImportacion.VALIDAR);
        when(properties.getSelectedPeriods()).thenReturn(Collections.emptyList());

        runner.run();

        verify(parser, never()).parse(any(Path.class), any(Charset.class));
        verify(handler, never()).handle(any());
        
        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("No se configuraron periodos para la importación histórica."));
    }

    @Test
    void testPeriodoInexistenteGeneraError(CapturedOutput output) throws Exception {
        when(properties.getMode()).thenReturn(ModoImportacion.VALIDAR);
        when(properties.getSelectedPeriods()).thenReturn(Arrays.asList("2025-12", "2027-01")); // 2027 no existe
        when(properties.getFiles()).thenReturn(allFiles);

        runner.run();

        verify(parser, never()).parse(any(Path.class), any(Charset.class));
        verify(handler, never()).handle(any());
        
        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("El periodo seleccionado 2027-01 no existe en la lista de archivos configurados."));
    }

    @Test
    void testMantieneOrdenCronologico() throws Exception {
        when(properties.getMode()).thenReturn(ModoImportacion.VALIDAR);
        when(properties.getSelectedPeriods()).thenReturn(Arrays.asList("2026-01", "2025-12", "2026-02"));
        when(properties.getFiles()).thenReturn(allFiles);
        when(properties.getDirectory()).thenReturn("/tmp/carteras");
        when(properties.getDefaultCharset()).thenReturn("UTF-8");
        
        when(hashService.calcularSha256(any(Path.class))).thenReturn("fakehash");
        when(parser.parse(any(Path.class), any(Charset.class))).thenReturn(Collections.emptyList());
        when(validator.validar(any(), any(), any())).thenReturn(new ReporteValidacionCsv(100));

        runner.run();

        // Capturar los comandos de importación
        org.mockito.ArgumentCaptor<ImportarCarteraHistoricaCommand> captor = org.mockito.ArgumentCaptor.forClass(ImportarCarteraHistoricaCommand.class);
        verify(validator, times(3)).validar(captor.capture(), any(), any());

        List<ImportarCarteraHistoricaCommand> comandos = captor.getAllValues();
        org.junit.jupiter.api.Assertions.assertEquals(3, comandos.size());
        org.junit.jupiter.api.Assertions.assertEquals(YearMonth.of(2025, 12), comandos.get(0).periodo());
        org.junit.jupiter.api.Assertions.assertEquals(YearMonth.of(2026, 1), comandos.get(1).periodo());
        org.junit.jupiter.api.Assertions.assertEquals(YearMonth.of(2026, 2), comandos.get(2).periodo());
    }

    @Test
    void testImportarProcesaSolamenteMesesSeleccionados(CapturedOutput output) throws Exception {
        when(properties.getMode()).thenReturn(ModoImportacion.IMPORTAR);
        when(properties.getSelectedPeriods()).thenReturn(Arrays.asList("2025-12", "2026-02"));
        when(properties.getFiles()).thenReturn(allFiles);
        when(properties.getDirectory()).thenReturn("/tmp/carteras");
        when(properties.getDefaultCharset()).thenReturn("UTF-8");
        
        when(handler.handle(any())).thenReturn(new ResultadoImportacionHistorica(YearMonth.of(2025, 12), true, 100, 100, ""));

        runner.run();

        verify(parser, never()).parse(any(Path.class), any(Charset.class)); // Importar usa handler, no parsea aqui directamente
        verify(handler, times(2)).handle(any());
        
        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("Modo IMPORTAR: ejecutando flujo completo para 2025-12"));
        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("Modo IMPORTAR: ejecutando flujo completo para 2026-02"));
    }
}
