package app.apaf.backend.features.session_management.update_password;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordCommand(
        @NotBlank(message = "La nueva contraseña no puede estar vacía")
        String newPassword,

        @NotBlank(message = "Debes confirmar tu nueva contraseña")
        String confirmNewPassword
) {
}
