package app.apaf.backend.features.cartera_management.importacionhistorica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cartera_importacion_historica")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarteraImportacionHistorica {

    @Id
    @GeneratedValue
    @Column(name = "id_importacion", columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID idImportacion;

    @Column(name = "mes_corte", nullable = false)
    private LocalDate mesCorte;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "hash_sha256", nullable = false, length = 64)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String hashSha256;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "total_filas", nullable = false)
    private int totalFilas;

    @Column(name = "filas_validas", nullable = false)
    private int filasValidas;

    @Column(name = "filas_insertadas", nullable = false)
    private int filasInsertadas;

    @Column(name = "filas_calculadas", nullable = false)
    private int filasCalculadas;

    @Column(name = "filas_rechazadas", nullable = false)
    private int filasRechazadas;

    @Column(name = "version_importador", length = 50)
    private String versionImportador;

    @Column(name = "ejecutado_por", nullable = false, length = 100)
    private String ejecutadoPor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "codigo_error", length = 80)
    private String codigoError;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;
}
