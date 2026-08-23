package app.apaf.backend.features.risk_management.deactivate_limit;

import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteVersionRepository;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteVersionEntity;
import app.apaf.backend.features.risk_management.domain.AccionLimite;

@Service
public class DesactivarLimiteRiesgoHandler {
    private final RiesgoLimiteRepository limiteRepository;
    private final RiesgoLimiteVersionRepository versionRepository;
    private final UserRepository userRepository;

    public DesactivarLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository,
            RiesgoLimiteVersionRepository versionRepository, UserRepository userRepository) {
        this.limiteRepository = limiteRepository;
        this.versionRepository = versionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RiesgoLimiteActionResponse handle(UUID idLimite, DesactivarLimiteRiesgoCommand command, String actor) {
        User user = userRepository.findByEmail(actor)
                .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("Usuario no encontrado"));
        Long realizadoPor = user.getIdUser();
        String actorReal = user.getFullName();
        try {
            RiesgoLimiteEntity limite = limiteRepository.findById(idLimite)
                    .orElseThrow(() -> new RiesgoExceptions.LimiteNoEncontradoException("Limite inexistente"));

            RiesgoLimiteVersionEntity actualVersion = versionRepository.findByRiesgoLimiteAndVigenteHastaIsNull(limite)
                    .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("No hay version activa"));

            actualVersion.closeVersion();
            versionRepository.save(actualVersion);

            limite.markUpdated();
            limiteRepository.save(limite);

            Integer newVersionNum = actualVersion.getNumeroVersion() + 1;

            RiesgoLimiteVersionEntity newVersion = new RiesgoLimiteVersionEntity(
                    limite, newVersionNum, actualVersion.getTipoLimite(), actualVersion.getLimitePorcentaje(), false,
                    AccionLimite.DESACTIVACION,
                    realizadoPor, actorReal, "USUARIO");
            versionRepository.save(newVersion);

            RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
            resp.setIdLimite(limite.getIdLimite());
            resp.setNumeroVersion(newVersionNum);
            resp.setMensaje("Limite desactivado exitosamente");
            return resp;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RiesgoExceptions.ConflictoVersionException("Version concurrente modificada");
        }
    }
}
