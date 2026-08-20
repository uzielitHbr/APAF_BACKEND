package app.apaf.backend.features.quarterly_analysis.domain.model;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.EstadoEjecucionTrimestral;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_ejecucion")
public class AnalisisTrimestralEjecucion {

    @Id
    @Column(name = "id_ejecucion")
    private UUID idEjecucion;

    @Column(name = "mes_corte", nullable = false)
    private LocalDate mesCorte;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEjecucionTrimestral estado;

    @Column(name = "version_formula", nullable = false, length = 50)
    private String versionFormula;

    @Column(name = "total_registros", nullable = false)
    private Long totalRegistros;

    @Column(name = "generado_por")
    private Long generadoPor;

    @Column(name = "actor", nullable = false, length = 150)
    private String actor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "codigo_error", length = 80)
    private String codigoError;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @Version
    @Column(name = "version_lock", nullable = false)
    private Long versionLock;

    public AnalisisTrimestralEjecucion() {
    }

    public UUID getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(UUID idEjecucion) {
        this.idEjecucion = idEjecucion;
    }

    public LocalDate getMesCorte() {
        return mesCorte;
    }

    public void setMesCorte(LocalDate mesCorte) {
        this.mesCorte = mesCorte;
    }

    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoEjecucionTrimestral getEstado() {
        return estado;
    }

    public void setEstado(EstadoEjecucionTrimestral estado) {
        this.estado = estado;
    }

    public String getVersionFormula() {
        return versionFormula;
    }

    public void setVersionFormula(String versionFormula) {
        this.versionFormula = versionFormula;
    }

    public Long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Long getGeneradoPor() {
        return generadoPor;
    }

    public void setGeneradoPor(Long generadoPor) {
        this.generadoPor = generadoPor;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public Long getVersionLock() {
        return versionLock;
    }

    public void setVersionLock(Long versionLock) {
        this.versionLock = versionLock;
    }
}
