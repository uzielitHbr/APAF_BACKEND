package app.apaf.backend.features.risk_management.list_limits;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteVersionEntity;
import app.apaf.backend.features.risk_management.domain.repository.RiesgoLimiteVersionRepository;
import app.apaf.backend.features.risk_management.analysis.RiesgoAnalisisReadRepository;
import app.apaf.backend.features.risk_management.analysis.RiesgoSegmentoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ListarLimitesHandler {
    private final RiesgoLimiteVersionRepository repository;
    private final RiesgoAnalisisReadRepository analisisRepository;

    public ListarLimitesHandler(RiesgoLimiteVersionRepository repository, RiesgoAnalisisReadRepository analisisRepository) {
        this.repository = repository;
        this.analisisRepository = analisisRepository;
    }

    @Transactional(readOnly = true)
    public ListarLimitesResponse handle(String agrupacionStr, String mesCorteStr, String search, int page, int size) {
        AgrupacionRiesgo agrupacion = null;
        if (agrupacionStr != null && !agrupacionStr.trim().isEmpty()) {
            try {
                agrupacion = AgrupacionRiesgo.valueOf(agrupacionStr.toUpperCase());
            } catch (Exception e) {
                throw new app.apaf.backend.features.risk_management.domain.exception.RiesgoExceptions.ParametroInvalidoException("Agrupacion invalida");
            }
        }
        
        LocalDate mesCorte;
        if (mesCorteStr != null && !mesCorteStr.trim().isEmpty()) {
            try {
                mesCorte = YearMonth.parse(mesCorteStr).atDay(1);
            } catch (Exception e) {
                try {
                    mesCorte = LocalDate.parse(mesCorteStr + "-01");
                } catch (Exception ex) {
                    mesCorte = LocalDate.now().withDayOfMonth(1);
                }
            }
        } else {
            mesCorte = LocalDate.now().withDayOfMonth(1);
        }

        PageRequest pr = PageRequest.of(page - 1, size);
        Page<RiesgoLimiteVersionEntity> versionPage;
        
        if (agrupacion != null) {
            if (search != null && !search.trim().isEmpty()) {
                versionPage = repository.findActiveByAgrupacionAndSearch(agrupacion, search.trim(), pr);
            } else {
                versionPage = repository.findActiveByAgrupacion(agrupacion, pr);
            }
        } else {
            if (search != null && !search.trim().isEmpty()) {
                versionPage = repository.findActiveBySearch(search.trim(), pr);
            } else {
                versionPage = repository.findActiveAll(pr);
            }
        }
        
        Set<AgrupacionRiesgo> agrupacionesPresentes = versionPage.getContent().stream()
                .map(v -> v.getRiesgoLimite().getAgrupacion())
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
        
        response.setDatos(versionPage.getContent().stream().map(v -> {
            ListarLimitesResponse.LimiteDto dto = new ListarLimitesResponse.LimiteDto();
            dto.setIdLimite(v.getRiesgoLimite().getIdLimite());
            dto.setClave(v.getRiesgoLimite().getClave());
            dto.setIdentificacion(v.getRiesgoLimite().getIdentificacion());
            dto.setTipoLimite(v.getTipoLimite().name());
            dto.setLimiteEstablecidoPorcentaje(v.getLimitePorcentaje());
            dto.setActivo(v.getActivo());
            dto.setNumeroVersion(v.getNumeroVersion());
            dto.setVigenteDesde(v.getVigenteDesde());
            
            String key = v.getRiesgoLimite().getAgrupacion().name() + "_" + v.getRiesgoLimite().getClave();
            dto.setNumCreditos(numCreditosMap.getOrDefault(key, 0L));
            return dto;
        }).collect(Collectors.toList()));
        
        ListarLimitesResponse.Meta meta = new ListarLimitesResponse.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setTotalElements(versionPage.getTotalElements());
        meta.setTotalPages(versionPage.getTotalPages());
        response.setMeta(meta);
        
        return response;
    }
}
