package app.apaf.backend.features.user_management.resend_setup_email;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
@RequiredArgsConstructor

@Tag(name = "Gestión de Usuarios", description = "Operación para los perfiles del sistema (Solo ADMIN)")
public class ResendEmailController {

    private final ResendEmailHandler resendEmailHandler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/resend-invitation")

    @Operation(
            summary = "Reenviar invitación a usuario nuevo",
            description = "Genera un nuevo token de 6 horas y reenvía el correo de bienvenida. Solo válido para usuarios PENDIENTES."
    )
    public ResponseEntity<ResendEmailResult> resendEmailSetUp(
            @Valid @RequestBody ResendEmailCommand resendEmailCommand
    ){
        ResendEmailResult result = resendEmailHandler.resendSetUpEmail(resendEmailCommand);

        return ResponseEntity.ok(result);
    }
}
