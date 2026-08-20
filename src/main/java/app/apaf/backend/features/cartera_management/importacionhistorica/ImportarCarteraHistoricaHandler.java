package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportarCarteraHistoricaHandler {

    private final CarteraCsvParser parser;
    private final CarteraCsvValidator validator;
    private final PersistirCarteraMensualService persistencia;
    private final ImportacionHashService hashService;
    private final CarteraImportacionHistoricaRepository auditoriaRepository;

    public ResultadoImportacionHistorica handle(ImportarCarteraHistoricaCommand command) {
        String hash = hashService.calcularSha256(command.archivo());

        List<CarteraCsvRow> filas = parser.parse(command.archivo(), command.charset());

        ReporteValidacionCsv reporte = validator.validar(command, filas, hash);
        reporte.throwIfInvalid();

        CarteraImportacionHistorica auditoria = CarteraImportacionHistorica.builder()
                .mesCorte(command.periodo().atDay(1))
                .fechaCorte(command.periodo().atEndOfMonth())
                .nombreArchivo(command.archivo().getFileName().toString())
                .hashSha256(hash)
                .estado("VALIDADA") // Cambiará a COMPLETADA/FALLIDA en la persistencia
                .totalFilas(filas.size())
                .filasValidas(filas.size()) // Ya que validó todo exitosamente
                .filasInsertadas(0)
                .filasCalculadas(0)
                .filasRechazadas(0)
                .versionImportador("1.0")
                .ejecutadoPor("EQUIPO_DESARROLLO")
                .fechaInicio(LocalDateTime.now())
                .build();

        auditoria = auditoriaRepository.save(auditoria);

        return persistencia.persistir(command, filas, reporte, auditoria);
    }
}
