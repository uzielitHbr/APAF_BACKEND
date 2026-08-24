package app.apaf.backend.features.risk_management.update_limit;

import app.apaf.backend.features.risk_management.domain.*;
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

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteHistorialEntity;

@Service
public class ActualizarLimiteRiesgoHandler {
    private final RiesgoLimiteRepository limiteRepository;
    private final RiesgoLimiteHistorialRepository historialRepository;
    private final UserRepository userRepository;

    public ActualizarLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository,
            RiesgoLimiteHistorialRepository historialRepository, UserRepository userRepository) {
        this.limiteRepository = limiteRepository;
        this.historialRepository = historialRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RiesgoLimiteActionResponse handle(UUID idLimite, ActualizarLimiteRiesgoCommand command, String actor) {
        User user = userRepository.findByEmail(actor)
                .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("Usuario no encontrado"));
        Long realizadoPor = user.getIdUser();
        String actorReal = user.getFullName();
        if (command.getLimiteEstablecidoPorcentaje() == null
                || command.getLimiteEstablecidoPorcentaje().compareTo(java.math.BigDecimal.ZERO) < 0
                || command.getLimiteEstablecidoPorcentaje().compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new RiesgoExceptions.LimiteInvalidoException("Porcentaje fuera de 0-100");
        }

        try {
            RiesgoLimiteEntity limite = limiteRepository.findById(idLimite)
                    .orElseThrow(() -> new RiesgoExceptions.LimiteNoEncontradoException("Limite inexistente"));

            

            TipoLimite tipo;
            try {
                tipo = TipoLimite.valueOf(command.getTipoLimite().toUpperCase());
            } catch (Exception e) {
                throw new RiesgoExceptions.ParametroInvalidoException("Tipo limite invalido");
            }

            java.math.BigDecimal anterior = limite.getPorcentajeActual();
            limite.updateLimite(tipo, command.getLimiteEstablecidoPorcentaje());
            limiteRepository.save(limite);

            RiesgoLimiteHistorialEntity newVersion = new RiesgoLimiteHistorialEntity(
                    limite, AccionLimite.ACTUALIZACION, anterior, command.getLimiteEstablecidoPorcentaje(), "Actualización de límite",
                    realizadoPor, actorReal);
            historialRepository.save(newVersion);

            RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
            resp.setIdLimite(limite.getIdLimite());
            
            resp.setMensaje("Limite actualizado exitosamente");
            return resp;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RiesgoExceptions.ConflictoVersionException("Version concurrente modificada");
        }
    }
}
