package app.apaf.backend.features.risk_management.create_limit;

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteVersionEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteVersionRepository;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import app.apaf.backend.features.risk_management.shared.RiesgoLimiteActionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.text.Normalizer;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.AccionLimite;
import app.apaf.backend.features.risk_management.domain.TipoLimite;

@Service
public class CrearLimiteRiesgoHandler {
    private final RiesgoLimiteRepository limiteRepository;
    private final RiesgoLimiteVersionRepository versionRepository;

    public CrearLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository,
            RiesgoLimiteVersionRepository versionRepository) {
        this.limiteRepository = limiteRepository;
        this.versionRepository = versionRepository;
    }

    private String normalizarClave(String clave) {
        if (clave == null)
            return "";
        String normalized = Normalizer.normalize(clave.trim().toUpperCase(), Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.replaceAll("\\s+", " ");
    }

    @Transactional
    public RiesgoLimiteActionResponse handle(CrearLimiteRiesgoCommand command, String actor) {
        if (command.getLimiteEstablecidoPorcentaje() == null
                || command.getLimiteEstablecidoPorcentaje().compareTo(java.math.BigDecimal.ZERO) < 0
                || command.getLimiteEstablecidoPorcentaje().compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new RiesgoExceptions.LimiteInvalidoException("Porcentaje fuera de 0-100");
        }

        AgrupacionRiesgo agrupacion;
        try {
            agrupacion = AgrupacionRiesgo.valueOf(command.getAgrupacion().toUpperCase());
        } catch (Exception e) {
            throw new RiesgoExceptions.ParametroInvalidoException("Agrupacion invalida");
        }

        String claveNorm = normalizarClave(command.getClave());

        Optional<RiesgoLimiteEntity> existing = limiteRepository.findByAgrupacionAndClave(agrupacion, claveNorm);
        if (existing.isPresent()) {
            throw new RiesgoExceptions.LimiteDuplicadoException("Clave duplicada en agrupacion");
        }

        RiesgoLimiteEntity limite = new RiesgoLimiteEntity(agrupacion, claveNorm, command.getIdentificacion());
        limite = limiteRepository.save(limite);

        TipoLimite tipo;
        try {
            tipo = TipoLimite.valueOf(command.getTipoLimite().toUpperCase());
        } catch (Exception e) {
            throw new RiesgoExceptions.ParametroInvalidoException("Tipo limite invalido");
        }

        RiesgoLimiteVersionEntity version = new RiesgoLimiteVersionEntity(
                limite, 1, tipo, command.getLimiteEstablecidoPorcentaje(), true, AccionLimite.CREACION,
                null, actor, "USUARIO");
        versionRepository.save(version);

        RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
        resp.setIdLimite(limite.getIdLimite());
        resp.setNumeroVersion(1);
        resp.setMensaje("Limite creado exitosamente");
        return resp;
    }
}
