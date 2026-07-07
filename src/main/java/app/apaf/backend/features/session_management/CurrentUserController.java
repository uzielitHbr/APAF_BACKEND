package app.apaf.backend.features.session_management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/current-user")
@RequiredArgsConstructor
@Tag(name = "Manejo de sesion activa")
public class CurrentUserController {

    private final CurrentUserQueryHandler currentUserQueryHandler;

    @GetMapping
    @Operation(
            summary = "Obtener datos del usuario en sesión",
            description = "Permite recuperar los datos del perfil usando solo el token JWT al recargar la página."
    )
    public ResponseEntity<CurrentUserResult> getCurrentUser() {
        return ResponseEntity.ok(currentUserQueryHandler.currentUsers());
    }

}
