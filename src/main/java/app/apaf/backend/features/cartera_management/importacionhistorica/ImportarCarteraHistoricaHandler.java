package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.net.Authenticator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import app.apaf.backend.domain.users.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import app.apaf.backend.domain.users.User;

@Service
@RequiredArgsConstructor
public class ImportarCarteraHistoricaHandler {

    private final CarteraCsvParser parser;
    private final CarteraCsvValidator validator;
    private final PersistirCarteraMensualService persistencia;
    private final ImportacionHashService hashService;
    private final CarteraImportacionHistoricaRepository auditoriaRepository;
    private final UserRepository userRepository;

    public ResultadoImportacionHistorica handle(ImportarCarteraHistoricaCommand command) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User usuario = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String idUsuario = String.valueOf(usuario.getIdUser());

        String hash = hashService.calcularSha256(command.archivo());

        List<CarteraCsvRow> filas = parser.parse(command.archivo(), command.charset());

        ReporteValidacionCsv reporte = validator.validar(command, filas, hash);
        reporte.throwIfInvalid();

        CarteraImportacionHistorica auditoria = CarteraImportacionHistorica.builder()
                .mesCorte(command.mesCorte().atDay(1))
                .fechaCorte(command.mesCorte().atEndOfMonth())
                .nombreArchivo(command.nombreArchivo())
                .hashSha256(hash)
                .estado("VALIDADA") // Cambiará a COMPLETADA/FALLIDA en la persistencia
                .totalFilas(filas.size())
                .filasValidas(filas.size()) // Ya que validó todo exitosamente
                .filasInsertadas(0)
                .filasCalculadas(0)
                .filasRechazadas(0)
                .versionImportador("1.0")
                .ejecutadoPor(idUsuario)
                .fechaInicio(LocalDateTime.now())
                .build();

        auditoria = auditoriaRepository.save(auditoria);

        return persistencia.persistir(command, filas, reporte, auditoria);
    }
}
