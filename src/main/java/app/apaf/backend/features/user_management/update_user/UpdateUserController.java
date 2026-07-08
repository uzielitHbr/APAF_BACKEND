package app.apaf.backend.features.user_management.update_user;


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
@Tag(name = "Gestión de Usuarios")
public class UpdateUserController {

    private final UpdateUserHandler updateUserHandler;

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualiza el perfil del usuario seleccionado ",
            description = "Uso de PUT ya que el modal de acualizar carga los datos del usuaios , lo que manda de regreso toda la información del usuario")
    public ResponseEntity<UpdateUserResult> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {

        UpdateUserCommand updateUserCommand = new UpdateUserCommand(
                id,
                request.fullName(),
                request.phoneNumber(),
                request.idRole()
        );
        return ResponseEntity.ok(updateUserHandler.updateUser(updateUserCommand));
    }


}
