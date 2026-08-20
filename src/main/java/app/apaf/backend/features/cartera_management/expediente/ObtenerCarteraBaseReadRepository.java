package app.apaf.backend.features.cartera_management.expediente;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ObtenerCarteraBaseReadRepository extends Repository<CarteraDatos, UUID> {
    List<CarteraDatos> findByNumeroSocioAndMesCorte(String numeroSocio, LocalDate mesCorte);
}
