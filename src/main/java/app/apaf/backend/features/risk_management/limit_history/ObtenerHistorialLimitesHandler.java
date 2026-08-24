package app.apaf.backend.features.risk_management.limit_history;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteHistorialEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteHistorialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerHistorialLimitesHandler {
    private final RiesgoLimiteHistorialRepository repository;

    public ObtenerHistorialLimitesHandler(RiesgoLimiteHistorialRepository repository) {
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

        PageRequest pr = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "fechaMovimiento"));
        Page<RiesgoLimiteHistorialEntity> versionPage;

        if (search != null && !search.trim().isEmpty()) {
            versionPage = repository.findByAgrupacionAndSearch(agrupacion, search.trim(), pr);
        } else {
            versionPage = repository.findByAgrupacion(agrupacion, pr);
        }

        ObtenerHistorialLimitesResponse response = new ObtenerHistorialLimitesResponse();

        response.setDatos(versionPage.getContent().stream().map(v -> {
            ObtenerHistorialLimitesResponse.HistorialDto dto = new ObtenerHistorialLimitesResponse.HistorialDto();
            dto.setIdVersion(v.getIdHistorial());
            dto.setIdLimite(v.getRiesgoLimite().getIdLimite());
            dto.setAccion(v.getAccion().name());
            dto.setAgrupacion(v.getRiesgoLimite().getAgrupacion().name().toLowerCase());
            dto.setClave(v.getRiesgoLimite().getClave());
            dto.setIdentificacion(v.getRiesgoLimite().getIdentificacion());
            dto.setTipoLimite(v.getRiesgoLimite().getTipoLimite().name());
            dto.setPorcentajeAnterior(v.getPorcentajeAnterior());
            dto.setNuevoPorcentaje(v.getPorcentajeNuevo());
            dto.setActivo(v.getRiesgoLimite().getActivo());
            dto.setFechaModificacion(v.getFechaMovimiento());

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
