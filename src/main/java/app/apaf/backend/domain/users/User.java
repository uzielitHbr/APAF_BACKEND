package app.apaf.backend.domain.users;


import app.apaf.backend.domain.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUser;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String fullName;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "contrasenia", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "intentos_fallidos")
    private Integer failedAttempts =0;

    @Column (name = "cuenta_bloqueada")
    private boolean accountLocked =false;

    @Column(name = "tiempo_bloqueado")
    private LocalDateTime lockTime;

    @Column(name = "fecha_creacion")
    private LocalDateTime creationDay;


    @Column(name = "fecha_baja")
    private LocalDateTime deactivationDate;

    @Column(name = "token_recuperacion")
    private String recoveryToken;

    @Column(name = "expiracion_token")
    private LocalDateTime expirationToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_permisos",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private List<Permission> permissions;
}

