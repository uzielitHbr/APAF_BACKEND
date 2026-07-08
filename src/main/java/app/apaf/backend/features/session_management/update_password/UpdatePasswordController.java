package app.apaf.backend.features.session_management.update_password;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/session/update-password")
@CrossOrigin("*")
@RequiredArgsConstructor

@Tag(name = "Manejo de sesion activa",description = "Uso de los usuarios")
public class UpdatePasswordController {


    private final UpdatePasswordHandler updatePasswordHandler;

    @PatchMapping()
    @Operation(
            summary = "Actualizar contraseña de usuarios",
            description = "Permite al usuario cambiar su contraseña."
    )
    public ResponseEntity<UpdatePasswordResult> updatePassword(
            @Valid @RequestBody UpdatePasswordCommand updatePasswordCommand
    ) {
        return ResponseEntity.ok(updatePasswordHandler.updatePassword(updatePasswordCommand));
    }
}
