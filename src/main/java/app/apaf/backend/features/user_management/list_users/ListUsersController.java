package app.apaf.backend.features.user_management.list_users;


import app.apaf.backend.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin("*")

@Tag(name = "Gestión de Usuarios", description = "Operación para los perfiles del sistema (Solo ADMIN)")
public class ListUsersController {

    private final ListUsersHandler listUsersHandler;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list-users")
    @Operation(summary = "Obtener lista de usuarios", description = "Permite listar todos los usuarios o filtrarlos opcionalmente por su estado mediante la URL.")
    public ResponseEntity<ListUsersResult> listUsers(
            @RequestParam(required = false) UserStatus status
            ) {

        ListUsersResult result = listUsersHandler.getListUsersHandler(status);

        return ResponseEntity.ok(result);


    }

}
