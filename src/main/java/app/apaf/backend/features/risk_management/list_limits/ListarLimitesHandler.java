package app.apaf.backend.features.risk_management.list_limits;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteRepository;
import app.apaf.backend.features.risk_management.analysis.RiesgoAnalisisReadRepository;
import app.apaf.backend.features.risk_management.analysis.RiesgoSegmentoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ListarLimitesHandler {
    private final RiesgoLimiteRepository repository;
    private final RiesgoAnalisisReadRepository analisisRepository;
    private final JdbcTemplate jdbcTemplate;

    public ListarLimitesHandler(RiesgoLimiteRepository repository, RiesgoAnalisisReadRepository analisisRepository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.analisisRepository = analisisRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public ListarLimitesResponse handle(String agrupacionStr, String search, int page, int size) {
        AgrupacionRiesgo agrupacion = null;
        if (agrupacionStr != null && !agrupacionStr.trim().isEmpty()) {
            try {
                agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
            } catch (Exception e) {
                throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException("Agrupacion invalida");
            }
        }
        
        LocalDate mesCorte;
        try {
            java.sql.Date maxDate = jdbcTemplate.queryForObject("SELECT MAX(mes_corte) FROM view_riesgo_cartera_mensual", java.sql.Date.class);
            mesCorte = maxDate != null ? maxDate.toLocalDate() : LocalDate.now().withDayOfMonth(1);
        } catch (Exception e) {
            mesCorte = LocalDate.now().withDayOfMonth(1);
        }

        PageRequest pr = PageRequest.of(page - 1, size);
        Page<RiesgoLimiteEntity> limitesPage;
        
        if (agrupacion != null) {
            if (search != null && !search.trim().isEmpty()) {
                limitesPage = repository.findByAgrupacionAndSearch(agrupacion, search.trim(), pr);
            } else {
                limitesPage = repository.findByAgrupacion(agrupacion, pr);
            }
        } else {
            if (search != null && !search.trim().isEmpty()) {
                limitesPage = repository.findBySearch(search.trim(), pr);
            } else {
                limitesPage = repository.findAll(pr);
            }
        }
        
        Set<AgrupacionRiesgo> agrupacionesPresentes = limitesPage.getContent().stream()
                .map(RiesgoLimiteEntity::getAgrupacion)
                .collect(Collectors.toSet());
                
        Map<String, Long> numCreditosMap = new HashMap<>();
        for (AgrupacionRiesgo ag : agrupacionesPresentes) {
            Page<RiesgoSegmentoProjection> pageSegmentos = analisisRepository.obtenerAnalisisPorAgrupacion(ag, mesCorte, Pageable.unpaged());
            for (RiesgoSegmentoProjection p : pageSegmentos.getContent()) {
                numCreditosMap.put(ag.name() + "_" + p.getClave(), p.getNumeroCreditos());
            }
        }

        ListarLimitesResponse response = new ListarLimitesResponse();
        response.setAgrupacion(agrupacionStr != null ? agrupacionStr.toLowerCase() : null);
        
        response.setDatos(limitesPage.getContent().stream().map(v -> {
            ListarLimitesResponse.LimiteDto dto = new ListarLimitesResponse.LimiteDto();
            dto.setIdLimite(v.getIdLimite());
            dto.setClave(v.getClave());
            dto.setIdentificacion(v.getIdentificacion());
            dto.setTipoLimite(v.getTipoLimite().name());
            dto.setLimiteEstablecidoPorcentaje(v.getPorcentajeActual());
            dto.setActivo(v.getActivo());
            
            String key = v.getAgrupacion().name() + "_" + v.getClave();
            dto.setNumCreditos(numCreditosMap.getOrDefault(key, 0L));
            return dto;
        }).collect(Collectors.toList()));
        
        ListarLimitesResponse.Meta meta = new ListarLimitesResponse.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setTotalElements(limitesPage.getTotalElements());
        meta.setTotalPages(limitesPage.getTotalPages());
        response.setMeta(meta);
        
        return response;
    }
}
