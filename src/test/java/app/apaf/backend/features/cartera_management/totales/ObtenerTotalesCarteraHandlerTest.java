package app.apaf.backend.features.cartera_management.totales;

import app.apaf.backend.domain.cartera.exception.CarteraPeriodoInvalidoException;
import app.apaf.backend.domain.cartera.exception.CarteraPeriodoNoEncontradoException;
import app.apaf.backend.domain.cartera.exception.CarteraTotalesInconsistentesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerTotalesCarteraHandlerTest {

    @Mock
    private CarteraTotalesReadRepository repository;

    @InjectMocks
    private ObtenerTotalesCarteraHandler handler;

    private CarteraTotalesProjection projection;

    @BeforeEach
    void setUp() {
        projection = new CarteraTotalesProjection() {
            @Override public LocalDate getFechaCorteMinima() { return LocalDate.of(2026, 3, 31); }
            @Override public LocalDate getFechaCorteMaxima() { return LocalDate.of(2026, 3, 31); }
            @Override public Long getTotalBase() { return 100L; }
            @Override public Long getTotalCalculados() { return 100L; }
            @Override public BigDecimal getTotalCartera() { return BigDecimal.TEN; }
            @Override public BigDecimal getTotalMontoOriginal() { return BigDecimal.TEN; }
            @Override public Long getTotalCreditosVigentes() { return 90L; }
            @Override public Long getTotalCreditosVencidos() { return 10L; }
            @Override public BigDecimal getCapitalVigente() { return BigDecimal.TEN; }
            @Override public BigDecimal getCapitalVencido() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesVigentes() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesVencidos() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesOrden() { return BigDecimal.TEN; }
            @Override public BigDecimal getUltimosPagosCapital() { return BigDecimal.TEN; }
            @Override public BigDecimal getUltimosPagosInteres() { return BigDecimal.TEN; }
            @Override public Long getTotalDiasMora() { return 5L; }
            @Override public BigDecimal getGarantiaLiquida() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcParteCubierta() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcParteExpuesta() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcInteresCee() { return BigDecimal.TEN; }
        };
    }

    @Test
    void debeRetornarResponseCuandoEsExitoso() {
        when(repository.obtenerTotalesPorMesCorte(LocalDate.of(2026, 3, 1))).thenReturn(Optional.of(projection));

        CarteraTotalesResponse response = handler.handle("2026-03");

        assertThat(response).isNotNull();
        assertThat(response.fechaCorte()).isEqualTo(LocalDate.of(2026, 3, 31));
        assertThat(response.resumenGeneral().totalNumeroCreditos()).isEqualTo(100L);
    }

    @Test
    void debeLanzarInvalidoCuandoPeriodoNoTieneFormatoCorrecto() {
        assertThatThrownBy(() -> handler.handle("2026-3"))
                .isInstanceOf(CarteraPeriodoInvalidoException.class);
    }

    @Test
    void debeLanzarNoEncontradoCuandoTotalBaseEsCero() {
        CarteraTotalesProjection pCero = new CarteraTotalesProjection() {
            @Override public LocalDate getFechaCorteMinima() { return null; }
            @Override public LocalDate getFechaCorteMaxima() { return null; }
            @Override public Long getTotalBase() { return 0L; }
            @Override public Long getTotalCalculados() { return 0L; }
            @Override public BigDecimal getTotalCartera() { return null; }
            @Override public BigDecimal getTotalMontoOriginal() { return null; }
            @Override public Long getTotalCreditosVigentes() { return 0L; }
            @Override public Long getTotalCreditosVencidos() { return 0L; }
            @Override public BigDecimal getCapitalVigente() { return null; }
            @Override public BigDecimal getCapitalVencido() { return null; }
            @Override public BigDecimal getInteresesVigentes() { return null; }
            @Override public BigDecimal getInteresesVencidos() { return null; }
            @Override public BigDecimal getInteresesOrden() { return null; }
            @Override public BigDecimal getUltimosPagosCapital() { return null; }
            @Override public BigDecimal getUltimosPagosInteres() { return null; }
            @Override public Long getTotalDiasMora() { return 0L; }
            @Override public BigDecimal getGarantiaLiquida() { return null; }
            @Override public BigDecimal getEprcParteCubierta() { return null; }
            @Override public BigDecimal getEprcParteExpuesta() { return null; }
            @Override public BigDecimal getEprcInteresCee() { return null; }
        };

        when(repository.obtenerTotalesPorMesCorte(any())).thenReturn(Optional.of(pCero));

        assertThatThrownBy(() -> handler.handle("2026-03"))
                .isInstanceOf(CarteraPeriodoNoEncontradoException.class);
    }

    @Test
    void debeLanzarInconsistentesCuandoTotalBaseDifiereDeCalculados() {
        CarteraTotalesProjection pDiferente = new CarteraTotalesProjection() {
            @Override public LocalDate getFechaCorteMinima() { return LocalDate.of(2026, 3, 31); }
            @Override public LocalDate getFechaCorteMaxima() { return LocalDate.of(2026, 3, 31); }
            @Override public Long getTotalBase() { return 100L; }
            @Override public Long getTotalCalculados() { return 99L; }
            @Override public BigDecimal getTotalCartera() { return BigDecimal.TEN; }
            @Override public BigDecimal getTotalMontoOriginal() { return BigDecimal.TEN; }
            @Override public Long getTotalCreditosVigentes() { return 90L; }
            @Override public Long getTotalCreditosVencidos() { return 10L; }
            @Override public BigDecimal getCapitalVigente() { return BigDecimal.TEN; }
            @Override public BigDecimal getCapitalVencido() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesVigentes() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesVencidos() { return BigDecimal.TEN; }
            @Override public BigDecimal getInteresesOrden() { return BigDecimal.TEN; }
            @Override public BigDecimal getUltimosPagosCapital() { return BigDecimal.TEN; }
            @Override public BigDecimal getUltimosPagosInteres() { return BigDecimal.TEN; }
            @Override public Long getTotalDiasMora() { return 5L; }
            @Override public BigDecimal getGarantiaLiquida() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcParteCubierta() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcParteExpuesta() { return BigDecimal.TEN; }
            @Override public BigDecimal getEprcInteresCee() { return BigDecimal.TEN; }
        };

        when(repository.obtenerTotalesPorMesCorte(any())).thenReturn(Optional.of(pDiferente));

        assertThatThrownBy(() -> handler.handle("2026-03"))
                .isInstanceOf(CarteraTotalesInconsistentesException.class);
    }
}
