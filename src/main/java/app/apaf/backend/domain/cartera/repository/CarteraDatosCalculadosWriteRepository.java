package app.apaf.backend.domain.cartera.repository;

import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarteraDatosCalculadosWriteRepository extends JpaRepository<CarteraDatosCalculados, UUID> {
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.ocupacionAgrupada FROM CarteraDatosCalculados c WHERE c.ocupacionAgrupada IS NOT NULL")
    java.util.List<String> findDistinctOcupacionAgrupada();
}
