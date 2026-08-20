package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarteraImportacionEstadoService {

    private final CarteraImportacionHistoricaRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarComoFallida(UUID idImportacion, String mensajeError) {
        repository.findById(idImportacion).ifPresent(auditoria -> {
            auditoria.setEstado("FALLIDA");
            auditoria.setMensajeError(mensajeError);
            repository.save(auditoria);
        });
    }
}
