package app.apaf.backend.features.quarterly_analysis.execution_history;

import app.apaf.backend.features.quarterly_analysis.domain.repository.CarteraAnaliticaReadRepository;
import app.apaf.backend.features.quarterly_analysis.exception.AnalisisTrimestralExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ObtenerHistorialEjecucionesHandler {

    private final CarteraAnaliticaReadRepository readRepo;

    public ObtenerHistorialEjecucionesHandler(CarteraAnaliticaReadRepository readRepo) {
        this.readRepo = readRepo;
    }

    public HistorialEjecucionesResponse handle(ObtenerHistorialEjecucionesQuery query) {
        LocalDate mesCorte;
        try {
            mesCorte = LocalDate.parse(query.mesCorte() + "-01");
        } catch (Exception e) {
            throw new AnalisisTrimestralExceptions.PeriodoInvalidoException("Formato de fecha inválido.");
        }

        long registros = readRepo.contarRegistrosPorMesCorte(mesCorte);
        if (registros == 0) {
            throw new AnalisisTrimestralExceptions.PeriodoSinCarteraException("No se encontraron registros de cartera para el periodo solicitado.");
        }

        HistorialEjecucionesResponse.Meta meta = new HistorialEjecucionesResponse.Meta(
            query.mesCorte(), query.page(), query.size(), 0L, 0, "HISTORIAL_EJECUCIONES"
        );
        
        return new HistorialEjecucionesResponse(meta, List.of());
    }
}
