package app.apaf.backend.features.user_management.resend_setup_email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResendEmailCommand(
        @NotNull(message = "Este campo no puede estar vacio")
        @Email(message = "El formato es invalido .Intenta con un correo valido")
        @Size(max = 100, message = "Limite de caracteres alcanzado")
        @NotBlank(message = "Este campo no puede tener espacios en blanco")
        String email
) {
}
