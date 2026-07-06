package app.apaf.backend.features.auth.reset_password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetPasswordCommand(
        @NotBlank(message = "Alerta! . El token no puede estar vacio ")
        String token,
        @NotNull(message = "Este campo no puede estar vacio")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String newPassword

) {
}
