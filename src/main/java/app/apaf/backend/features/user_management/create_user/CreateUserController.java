package app.apaf.backend.features.user_management.create_user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios", description = "Operación para los perfiles del sistema (Solo ADMIN)")
public class CreateUserController {
    private  final CreateUserHandler createUserHandler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @Operation(
            summary = "Registrar un nuevo usuario ",
            description = "Solo accesible para el rol ADMIN. Crea un usuario en estado PENDIENTE y  automáticamente  envia  correo de bienvenida para que configure su contraseña."
    )
    public ResponseEntity<CreateUserResult> createUser(@Valid @RequestBody CreateUserCommand command) {
        System.out.println("User created by : ");
        CreateUserResult response = createUserHandler.createUser(command);
        return ResponseEntity.ok(response);
    }

}
