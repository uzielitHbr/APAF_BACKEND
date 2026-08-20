package app.apaf.backend.features.cartera_management.calculados;

import app.apaf.backend.domain.cartera.entity.CarteraDatosCalculados;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObtenerCarteraCalculadaReadRepository extends Repository<CarteraDatosCalculados, UUID> {

    // Solo seleccionamos los campos de calculados, evitando inicializar
    // carteraDatos
    @Query("SELECT c FROM CarteraDatosCalculados c WHERE c.idAnalisisMensual = :idAnalisisMensual")
    Optional<CarteraDatosCalculados> findByIdAnalisisMensual(@Param("idAnalisisMensual") UUID idAnalisisMensual);

    @Query("SELECT c FROM CarteraDatosCalculados c JOIN c.carteraDatos d WHERE d.numeroSocio = :numeroSocio AND d.mesCorte = :mesCorte")
    List<CarteraDatosCalculados> findByNumeroSocioAndMesCorte(@Param("numeroSocio") String numeroSocio, @Param("mesCorte") LocalDate mesCorte);
}
