package app.apaf.backend.features.cartera_management.totales;

import app.apaf.backend.domain.cartera.exception.CarteraPeriodoInvalidoException;
import app.apaf.backend.domain.cartera.exception.CarteraPeriodoNoEncontradoException;
import app.apaf.backend.domain.cartera.exception.CarteraTotalesInconsistentesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObtenerTotalesCarteraHandler {

    private final CarteraTotalesReadRepository repository;

    public CarteraTotalesResponse handle(String mesCorteStr) {
        YearMonth periodo;
        try {
            periodo = YearMonth.parse(mesCorteStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new CarteraPeriodoInvalidoException("El periodo proporcionado es inválido: " + mesCorteStr);
        }

        LocalDate mesCorte = periodo.atDay(1);

        Optional<CarteraTotalesProjection> resultOpt = repository.obtenerTotalesPorMesCorte(mesCorte);

        if (resultOpt.isEmpty() || resultOpt.get().getTotalBase() == null || resultOpt.get().getTotalBase() == 0) {
            throw new CarteraPeriodoNoEncontradoException("No se encontraron registros de cartera para el periodo: " + mesCorteStr);
        }

        CarteraTotalesProjection result = resultOpt.get();

        if (!result.getTotalBase().equals(result.getTotalCalculados())) {
            throw new CarteraTotalesInconsistentesException(String.format("Inconsistencia en totales: Registros base (%d) no coinciden con los calculados (%d)", result.getTotalBase(), result.getTotalCalculados()));
        }

        LocalDate finDeMes = periodo.atEndOfMonth();
        if (result.getFechaCorteMinima() == null || !result.getFechaCorteMinima().equals(result.getFechaCorteMaxima()) || !result.getFechaCorteMaxima().equals(finDeMes)) {
            throw new CarteraTotalesInconsistentesException("Inconsistencia en fechas de corte: La fecha mínima y máxima deben coincidir exactamente con el fin de mes del periodo solicitado.");
        }

        return new CarteraTotalesResponse(
                result.getFechaCorteMaxima(),
                new ResumenGeneralResponse(
                        result.getTotalCartera(),
                        result.getTotalMontoOriginal(),
                        result.getTotalBase(),
                        result.getTotalCreditosVigentes(),
                        result.getTotalCreditosVencidos()
                ),
                new SaldosDevengadosResponse(
                        result.getCapitalVigente(),
                        result.getCapitalVencido(),
                        result.getInteresesVigentes(),
                        result.getInteresesVencidos(),
                        result.getInteresesOrden()
                ),
                new FlujosRecuperacionResponse(
                        result.getUltimosPagosCapital(),
                        result.getUltimosPagosInteres()
                ),
                new RiesgoYRegulatorioResponse(
                        result.getTotalDiasMora(),
                        result.getGarantiaLiquida(),
                        result.getEprcParteCubierta(),
                        result.getEprcParteExpuesta(),
                        result.getEprcInteresCee()
                )
        );
    }
}
