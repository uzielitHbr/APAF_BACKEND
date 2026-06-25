package app.apaf.backend.domain.users;


import app.apaf.backend.domain.enums.RoleUser;
import app.apaf.backend.domain.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


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


    @Column(name = "telefono",length = 15)
    private String phoneNumber;


    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20)
    private RoleUser role;

    @Column(name = "intentos_fallidos")
    private Integer failedAttempts =0;

    @Column (name = "cuenta_bloqueada")
    private boolean accountLocked =false;

    @Column(name = "tiempo_bloqueado")
    private LocalDateTime lockTime;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private LocalDateTime creationDay;


    @Column(name = "fecha_baja")
    private LocalDateTime deactivationDate;

    @Column(name = "token_recuperacion")
    private String verificationToken;

    @Column(name = "expiracion_token")
    private LocalDateTime expirationToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private User createdBy;


}

