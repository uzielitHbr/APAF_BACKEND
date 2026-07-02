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
@RequestMapping("/api/v1/users/create")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios", description = "Operación para los perfiles del sistema (Requiere JWT)")
public class CreateUserController {
    private  final CreateUserHandler createUserHandler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario ",
            description = "Solo accesible para el rol ADMIN. Crea un usuario en estado PENDIENTE y dispara automáticamente el envío del correo de bienvenida para que configure su contraseña."
    )
    public ResponseEntity<CreateUserResult> createUser(@Valid @RequestBody CreateUserCommand command) {
        System.out.println("User created by : ");
        CreateUserResult response = createUserHandler.createUser(command);
        return ResponseEntity.ok(response);
    }

}
