package app.apaf.backend.features.risk_management.reactivate_limit;

import app.apaf.backend.features.risk_management.domain.*;
import app.apaf.backend.features.risk_management.domain.entity.*;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteVersionRepository;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Service
public class ReactivarLimiteRiesgoHandler {
    private final RiesgoLimiteRepository limiteRepository;
    private final RiesgoLimiteVersionRepository versionRepository;

    public ReactivarLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository, RiesgoLimiteVersionRepository versionRepository) {
        this.limiteRepository = limiteRepository;
        this.versionRepository = versionRepository;
    }

    @Transactional
    public RiesgoLimiteActionResponse handle(UUID idLimite, ReactivarLimiteRiesgoCommand command, String actor) {
        if (command.getLimiteEstablecidoPorcentaje() == null || command.getLimiteEstablecidoPorcentaje().compareTo(java.math.BigDecimal.ZERO) < 0 || command.getLimiteEstablecidoPorcentaje().compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new RiesgoExceptions.LimiteInvalidoException("Porcentaje fuera de 0-100");
        }
        
        try {
            RiesgoLimiteEntity limite = limiteRepository.findById(idLimite)
                .orElseThrow(() -> new RiesgoExceptions.LimiteNoEncontradoException("Limite inexistente"));
            
            RiesgoLimiteVersionEntity actualVersion = versionRepository.findByRiesgoLimiteAndVigenteHastaIsNull(limite)
                .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("No hay version activa"));
                
            actualVersion.closeVersion();
            versionRepository.save(actualVersion);
            
            limite.markUpdated();
            limiteRepository.save(limite);
            
            TipoLimite tipo;
            try {
                tipo = TipoLimite.valueOf(command.getTipoLimite().toUpperCase());
            } catch (Exception e) {
                throw new RiesgoExceptions.ParametroInvalidoException("Tipo limite invalido");
            }
            
            Integer newVersionNum = actualVersion.getNumeroVersion() + 1;
            
            RiesgoLimiteVersionEntity newVersion = new RiesgoLimiteVersionEntity(
                limite, newVersionNum, tipo, command.getLimiteEstablecidoPorcentaje(), true, AccionLimite.REACTIVACION, 
                null, actor, "USUARIO"
            );
            versionRepository.save(newVersion);
            
            RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
            resp.setIdLimite(limite.getIdLimite());
            resp.setNumeroVersion(newVersionNum);
            resp.setMensaje("Limite reactivado exitosamente");
            return resp;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RiesgoExceptions.ConflictoVersionException("Version concurrente modificada");
        }
    }
}
