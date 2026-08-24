package app.apaf.backend.features.cartera_management.importacionhistorica.events;

import app.apaf.backend.domain.cartera.calculo.CarteraCalculationService;
import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import app.apaf.backend.domain.cartera.repository.CarteraDatosWriteRepository;
import app.apaf.backend.domain.cartera.repository.CarteraDatosCalculadosWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarteraCalculoEventListener {

    private final CarteraDatosWriteRepository baseRepository;
    private final CarteraDatosCalculadosWriteRepository calculadosRepository;
    private final CarteraCalculationService calculationService;

    @Async
    @EventListener
    @Transactional
    public void onCarteraImportada(CarteraImportadaEvent event) {
        log.info("Recibido evento CarteraImportadaEvent para periodo: {}", event.getPeriodo());
        LocalDate mesCorte = event.getPeriodo().atDay(1);
        LocalDate fechaCorteAnterior = event.getPeriodo().minusMonths(1).atEndOfMonth();

        List<CarteraDatos> sinCalcular = baseRepository.findByMesCorte(mesCorte).stream()
                .filter(base -> !calculadosRepository.existsById(base.getIdAnalisisMensual()))
                .collect(Collectors.toList());

        if (sinCalcular.isEmpty()) {
            log.info("No hay registros pendientes de calcular para el periodo {}", event.getPeriodo());
            return;
        }

        log.info("Calculando {} registros pendientes para el periodo {}", sinCalcular.size(), event.getPeriodo());

        List<CarteraDatosCalculados> calculados = sinCalcular.stream()
                .map(base -> calculationService.calcular(base, fechaCorteAnterior))
                .collect(Collectors.toList());

        calculadosRepository.saveAll(calculados);

        log.info("Cálculos completados para el periodo {}", event.getPeriodo());
    }
}
