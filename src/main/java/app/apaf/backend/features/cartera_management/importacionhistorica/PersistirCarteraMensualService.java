package app.apaf.backend.features.cartera_management.importacionhistorica;

import app.apaf.backend.domain.cartera.calculo.CarteraCalculationService;
import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import app.apaf.backend.domain.cartera.repository.CarteraDatosCalculadosWriteRepository;
import app.apaf.backend.domain.cartera.repository.CarteraDatosWriteRepository;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.PersistenciaImportacionException;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersistirCarteraMensualService {

    private final CarteraDatosWriteRepository baseRepository;
    private final CarteraDatosCalculadosWriteRepository calculadosRepository;
    private final CarteraCalculationService calculationService;
    private final CarteraImportacionEstadoService estadoService;
    private final CarteraImportacionHistoricaRepository auditoriaRepository;
    private final CarteraCsvMapper mapper;
    private final EntityManager entityManager;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    private final app.apaf.backend.features.cartera_management.importacionhistorica.events.CarteraImportadaEvent publishCarteraImportadaEvent(ImportarCarteraHistoricaCommand command) {
        return new app.apaf.backend.features.cartera_management.importacionhistorica.events.CarteraImportadaEvent(this, command.mesCorte());
    }

    @Transactional
    public ResultadoImportacionHistorica persistir(ImportarCarteraHistoricaCommand command,
            List<CarteraCsvRow> rows,
            ReporteValidacionCsv reporte,
            CarteraImportacionHistorica auditoria) {

        try {
            if (rows == null || rows.isEmpty()) {
                throw new IllegalStateException("No hay datos asociados para persistir.");
            }
            
            List<CarteraDatos> batchBase = new ArrayList<>();
            int totalInsertadas = 0;
            LocalDate fechaCorteAnterior = command.mesCorte().minusMonths(1).atEndOfMonth();

            for (CarteraCsvRow row : rows) {
                CarteraDatos base = mapper.map(row, command.mesCorte(), auditoria.getIdImportacion());
                batchBase.add(base);

                if (batchBase.size() >= command.batchSize()) {
                    totalInsertadas += procesarLote(batchBase, fechaCorteAnterior);
                    batchBase.clear();
                }
            }

            if (!batchBase.isEmpty()) {
                totalInsertadas += procesarLote(batchBase, fechaCorteAnterior);
            }

            auditoria.setEstado("COMPLETADA");
            auditoria.setFilasInsertadas(totalInsertadas);
            auditoria.setFilasCalculadas(totalInsertadas);
            auditoriaRepository.save(auditoria);

            if (eventPublisher != null) {
                eventPublisher.publishEvent(publishCarteraImportadaEvent(command));
            }

            return ResultadoImportacionHistorica.builder()
                    .periodo(command.mesCorte())
                    .exitoso(true)
                    .totalFilas(rows.size())
                    .filasInsertadas(totalInsertadas)
                    .mensaje("Importación completada correctamente")
                    .build();

        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            estadoService.marcarComoFallida(auditoria.getIdImportacion(), errorMsg);
            throw new PersistenciaImportacionException(
                    "Error persistiendo periodo " + command.mesCorte() + ": " + e.getMessage(), e);
        }
    }

    private int procesarLote(List<CarteraDatos> batchBase, LocalDate fechaCorteAnterior) {
        // Guardar lote base primero para generar los UUIDs si no lo hicimos nosotros
        List<CarteraDatos> savedBase = baseRepository.saveAll(batchBase);

        // Calcular y preparar lote de calculados
        for (CarteraDatos base : savedBase) {
            CarteraDatosCalculados calculado = calculationService.calcular(base, fechaCorteAnterior);
            entityManager.persist(calculado);
        }

        entityManager.flush();
        entityManager.clear();

        return savedBase.size();
    }
}
