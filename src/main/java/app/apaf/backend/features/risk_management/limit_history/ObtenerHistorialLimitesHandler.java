package app.apaf.backend.features.risk_management.limit_history;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteVersionEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerHistorialLimitesHandler {
    private final RiesgoLimiteVersionRepository repository;

    public ObtenerHistorialLimitesHandler(RiesgoLimiteVersionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ObtenerHistorialLimitesResponse handle(String agrupacionStr, String search, int page, int size) {
        AgrupacionRiesgo agrupacion;
        try {
            agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
        } catch (Exception e) {
            throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException(
                    "Agrupacion invalida");
        }

        PageRequest pr = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "fechaRegistro"));
        Page<RiesgoLimiteVersionEntity> versionPage;

        if (search != null && !search.trim().isEmpty()) {
            versionPage = repository.findHistoryByAgrupacionAndSearch(agrupacion, search.trim(), pr);
        } else {
            versionPage = repository.findHistoryByAgrupacion(agrupacion, pr);
        }

        ObtenerHistorialLimitesResponse response = new ObtenerHistorialLimitesResponse();

        response.setDatos(versionPage.getContent().stream().map(v -> {
            ObtenerHistorialLimitesResponse.HistorialDto dto = new ObtenerHistorialLimitesResponse.HistorialDto();
            dto.setIdVersion(v.getIdVersion());
            dto.setIdLimite(v.getRiesgoLimite().getIdLimite());
            dto.setAccion(v.getAccion().name());
            dto.setAgrupacion(v.getRiesgoLimite().getAgrupacion().name().toLowerCase());
            dto.setClave(v.getRiesgoLimite().getClave());
            dto.setIdentificacion(v.getRiesgoLimite().getIdentificacion());
            dto.setTipoLimite(v.getTipoLimite().name());
            dto.setPorcentajeAnterior(v.getLimitePorcentaje());
            dto.setNuevoPorcentaje(v.getLimitePorcentaje());
            dto.setActivo(v.getActivo());
            dto.setFechaModificacion(v.getFechaRegistro());

            ObtenerHistorialLimitesResponse.ActorDto actor = new ObtenerHistorialLimitesResponse.ActorDto();
            actor.setIdUsuario(v.getRealizadoPor());
            actor.setNombre(v.getActor());
            actor.setCorreo(v.getActor());
            dto.setRealizadoPor(actor);

            return dto;
        }).collect(Collectors.toList()));

        ObtenerHistorialLimitesResponse.Meta meta = new ObtenerHistorialLimitesResponse.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setTotalElements(versionPage.getTotalElements());
        meta.setTotalPages(versionPage.getTotalPages());
        response.setMeta(meta);

        return response;
    }
}
