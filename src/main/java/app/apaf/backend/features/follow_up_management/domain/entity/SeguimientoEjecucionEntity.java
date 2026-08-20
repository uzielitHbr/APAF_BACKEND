package app.apaf.backend.features.follow_up_management.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seguimiento_ejecucion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoEjecucionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ejecucion", updatable = false, nullable = false)
    private UUID idEjecucion;

    @Column(name = "mes_corte", unique = true, nullable = false)
    private LocalDate mesCorte;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion;
}
