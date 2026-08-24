package app.apaf.backend.features.risk_management.reactivate_limit;

import app.apaf.backend.features.risk_management.domain.*;
import app.apaf.backend.features.risk_management.domain.entity.*;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteHistorialRepository;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Service
public class ReactivarLimiteRiesgoHandler {
    private final RiesgoLimiteRepository limiteRepository;
    private final RiesgoLimiteHistorialRepository historialRepository;
    private final UserRepository userRepository;

    public ReactivarLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository,
            RiesgoLimiteHistorialRepository historialRepository, UserRepository userRepository) {
        this.limiteRepository = limiteRepository;
        this.historialRepository = historialRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RiesgoLimiteActionResponse handle(UUID idLimite, ReactivarLimiteRiesgoCommand command, String actor) {
        User user = userRepository.findByEmail(actor)
                .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("Usuario no encontrado"));
        Long realizadoPor = user.getIdUser();
        String actorReal = user.getFullName();

        try {
            RiesgoLimiteEntity limite = limiteRepository.findById(idLimite)
                    .orElseThrow(() -> new RiesgoExceptions.LimiteNoEncontradoException("Limite inexistente"));
            
            if (limite.getActivo()) {
                throw new RiesgoExceptions.LimiteInvalidoException("El límite ya se encuentra activo");
            }

            java.math.BigDecimal anterior = limite.getPorcentajeActual();
            limite.reactivar();
            limiteRepository.save(limite);

            RiesgoLimiteHistorialEntity newVersion = new RiesgoLimiteHistorialEntity(
                    limite, AccionLimite.REACTIVACION, anterior, anterior, command.getMotivo(),
                    realizadoPor, actorReal);
            historialRepository.save(newVersion);

            RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
            resp.setIdLimite(limite.getIdLimite());
            resp.setMensaje("Limite reactivado exitosamente");
            return resp;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RiesgoExceptions.ConflictoVersionException("Version concurrente modificada");
        }
    }
}
