package app.apaf.backend.features.cartera_eprc.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eprc_ejecucion")
@Getter
@Setter
@NoArgsConstructor
public class EprcEjecucionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ejecucion")
    private UUID idEjecucion;

    @Column(name = "mes_corte", nullable = false, unique = true)
    private LocalDate mesCorte;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion = LocalDateTime.now();

    @Column(name = "total_registros", nullable = false)
    private Long totalRegistros = 0L;

    @Column(name = "estado", nullable = false)
    private String estado;
}
