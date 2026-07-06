package app.apaf.backend.features.user_management.status_management;


import app.apaf.backend.domain.enums.UserStatus;
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
public class StatusManagementController {

    private final StatusManagementHandler statusManagementHandler;


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Activar o Desactivar usuario ",
            description = "Permite a un Administrador cambiar el estado de un empleado (ACTIVO / INACTIVO). Evita la auto desactivación del administrador logueado."
    )
    public ResponseEntity<StatusManagementResult> statusManagement(
            @PathVariable Long id,
            @Valid @RequestBody StatusManagementCommand statusManagementCommand){
        StatusManagementResult result = statusManagementHandler.updateStatus(id,statusManagementCommand);
        return ResponseEntity.ok(result);
    }


}
