package app.apaf.backend.features.risk_management.create_limit;

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteHistorialEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteHistorialRepository;
import app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.domain.users.User;
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
    private final RiesgoLimiteHistorialRepository historialRepository;
    private final UserRepository userRepository;

    public CrearLimiteRiesgoHandler(RiesgoLimiteRepository limiteRepository,
            RiesgoLimiteHistorialRepository historialRepository, UserRepository userRepository) {
        this.limiteRepository = limiteRepository;
        this.historialRepository = historialRepository;
        this.userRepository = userRepository;
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
        User user = userRepository.findByEmail(actor)
                .orElseThrow(() -> new RiesgoExceptions.DatosInconsistentesException("Usuario no encontrado"));
        Long realizadoPor = user.getIdUser();
        String actorReal = user.getFullName();
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

        TipoLimite tipo;
        try {
            tipo = TipoLimite.valueOf(command.getTipoLimite().toUpperCase());
        } catch (Exception e) {
            throw new RiesgoExceptions.ParametroInvalidoException("Tipo limite invalido");
        }

        Optional<RiesgoLimiteEntity> existing = limiteRepository.findByAgrupacionAndClave(agrupacion, claveNorm);
        RiesgoLimiteEntity limite;
        
        if (existing.isPresent()) {
            limite = existing.get();
            // UPSERT: Si ya existe (como los estáticos precargados o ya creados con NULL), actualizamos
            // Ojo: si ya tiene un porcentaje asignado, el front-end debería haber llamado a PUT (Actualizar), pero
            // al nivel de BD y handler, si lo mandan a POST (Crear), también se considera válido sobreescribir
            // (o podríamos lanzar error si porcentaje_actual != null, pero la regla dice "Actualiza el porcentaje_actual").
            if (limite.getPorcentajeActual() != null && limite.getActivo()) {
                throw new RiesgoExceptions.LimiteDuplicadoException("El límite ya se encuentra activo y configurado. Utilice la actualización.");
            }
            limite.updateLimite(tipo, command.getLimiteEstablecidoPorcentaje());
            limite = limiteRepository.save(limite);
        } else {
            limite = new RiesgoLimiteEntity(agrupacion, claveNorm, command.getIdentificacion(), tipo, command.getLimiteEstablecidoPorcentaje());
            limite = limiteRepository.save(limite);
        }

        RiesgoLimiteHistorialEntity version = new RiesgoLimiteHistorialEntity(
                limite, AccionLimite.CREACION, null, command.getLimiteEstablecidoPorcentaje(), "Creación de límite",
                realizadoPor, actorReal);
        historialRepository.save(version);

        RiesgoLimiteActionResponse resp = new RiesgoLimiteActionResponse();
        resp.setIdLimite(limite.getIdLimite());
        
        resp.setMensaje("Limite creado exitosamente");
        return resp;
    }
}
