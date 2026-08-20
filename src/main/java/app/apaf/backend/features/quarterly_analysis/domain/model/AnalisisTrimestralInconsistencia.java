package app.apaf.backend.features.quarterly_analysis.domain.model;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.SeveridadInconsistencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_inconsistencia")
public class AnalisisTrimestralInconsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inconsistencia")
    private Long idInconsistencia;

    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidad", nullable = false, length = 20)
    private SeveridadInconsistencia severidad;

    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "referencia", length = 200)
    private String referencia;

    @Column(name = "valor_esperado", columnDefinition = "TEXT")
    private String valorEsperado;

    @Column(name = "valor_obtenido", columnDefinition = "TEXT")
    private String valorObtenido;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "bloqueante", nullable = false)
    private Boolean bloqueante;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public AnalisisTrimestralInconsistencia() {
    }

    public Long getIdInconsistencia() {
        return idInconsistencia;
    }

    public void setIdInconsistencia(Long idInconsistencia) {
        this.idInconsistencia = idInconsistencia;
    }

    public UUID getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(UUID idEjecucion) {
        this.idEjecucion = idEjecucion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public SeveridadInconsistencia getSeveridad() {
        return severidad;
    }

    public void setSeveridad(SeveridadInconsistencia severidad) {
        this.severidad = severidad;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getValorEsperado() {
        return valorEsperado;
    }

    public void setValorEsperado(String valorEsperado) {
        this.valorEsperado = valorEsperado;
    }

    public String getValorObtenido() {
        return valorObtenido;
    }

    public void setValorObtenido(String valorObtenido) {
        this.valorObtenido = valorObtenido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getBloqueante() {
        return bloqueante;
    }

    public void setBloqueante(Boolean bloqueante) {
        this.bloqueante = bloqueante;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
