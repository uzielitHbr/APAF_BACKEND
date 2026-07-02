package app.apaf.backend.features.auth.set_password_newUsers;

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
public class SetPasswordController {

    private final SetPasswordHandler setPasswordHandler;

    @PatchMapping("/set-password")
    @Operation(
            summary = "Activar cuenta (Nuevos Usuarios)",
            description = "Recibe el token de invitación enviado por correo y la nueva contraseña. Válido únicamente para cuentas en estado PENDIENTE."
    )
    public ResponseEntity<SetPasswordResult> setPassword(@Valid @RequestBody SetPasswordCommand command) {
        SetPasswordResult result = setPasswordHandler.newPassword(command);
        return ResponseEntity.ok(result);
    }

}
