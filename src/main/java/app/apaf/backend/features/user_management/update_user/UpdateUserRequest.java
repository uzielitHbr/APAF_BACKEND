package app.apaf.backend.features.user_management.update_user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Captures the profile update data from the JSON body (excludes ID).
 */
public record UpdateUserRequest(
        @NotBlank(message = "El nombre completo no puede estar vacío")
        String fullName,

        String phoneNumber,

        @NotNull(message = "El ID del rol es obligatorio")
        Long idRole
) {
}
