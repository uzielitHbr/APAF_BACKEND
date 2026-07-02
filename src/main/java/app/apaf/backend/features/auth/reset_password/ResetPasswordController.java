package app.apaf.backend.features.auth.reset_password;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor

@Tag(name = "Autenticación y Seguridad", description = "Endpoints públicos para el acceso y recuperación de cuentas")
public class ResetPasswordController {

    private final ResetPasswordHandler  resetPasswordHandler;

    @PatchMapping("/reset-password")
    @Operation(
            summary = "Restablecer contraseña olvidada (Fase 2)",
            description = "Recibe el token de recuperación y la nueva contraseña. Al procesarse exitosamente, restablece los intentos fallidos, desbloquea la cuenta y elimina el token de seguridad."
    )
    public ResponseEntity<ResetPasswordResult> resetPassword(
           @Valid @RequestBody ResetPasswordCommand resetPasswordCommand
    ){
        ResetPasswordResult result = resetPasswordHandler.forgotPassword(resetPasswordCommand);
        return ResponseEntity.ok(result);
    }


}
