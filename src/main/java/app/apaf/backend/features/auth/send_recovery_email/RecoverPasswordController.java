package app.apaf.backend.features.auth.send_recovery_email;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

@Tag(name = "Autenticación y Seguridad", description = "Endpoints públicos para el acceso y recuperación de cuentas")
public class RecoverPasswordController {

    private final RecoverPasswordHandler recoverPasswordHandler;

    @PostMapping("/recover-password")
    @Operation(
            summary = "Solicitar recuperación de contraseña (Fase 1)",
            description = "Recibe el correo del usuario y le envía un token de recuperación . Exclusivo para usuarios en estado ACTIVO."
    )
    public ResponseEntity<RecoverPasswordResult> recoverPassword(@Valid @RequestBody RecoverPasswordCommand command) {

        RecoverPasswordResult result = recoverPasswordHandler.recoverPassword(command);

        return ResponseEntity.ok(result);
    }
}