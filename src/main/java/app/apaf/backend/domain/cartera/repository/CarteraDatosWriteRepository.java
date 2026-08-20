package app.apaf.backend.domain.cartera.repository;

import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface CarteraDatosWriteRepository extends JpaRepository<CarteraDatos, UUID> {
    boolean existsByMesCorteAndNumeroContrato(LocalDate mesCorte, String numeroContrato);
    java.util.List<CarteraDatos> findByMesCorte(LocalDate mesCorte);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.productoCredito FROM CarteraDatos c WHERE c.productoCredito IS NOT NULL")
    java.util.List<String> findDistinctProductoCredito();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.municipio FROM CarteraDatos c WHERE c.municipio IS NOT NULL")
    java.util.List<String> findDistinctMunicipio();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.estado FROM CarteraDatos c WHERE c.estado IS NOT NULL")
    java.util.List<String> findDistinctEstado();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.genero FROM CarteraDatos c WHERE c.genero IS NOT NULL")
    java.util.List<String> findDistinctGenero();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.sucursal FROM CarteraDatos c WHERE c.sucursal IS NOT NULL")
    java.util.List<String> findDistinctSucursal();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.modalidadPago FROM CarteraDatos c WHERE c.modalidadPago IS NOT NULL")
    java.util.List<String> findDistinctModalidadPago();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.tipoCarteraCalificacion FROM CarteraDatos c WHERE c.tipoCarteraCalificacion IS NOT NULL")
    java.util.List<String> findDistinctTipoClasificacion();
}
