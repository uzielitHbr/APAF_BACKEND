package app.apaf.backend.features.auth.recoverpassword;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecoverPasswordCommand(

        @NotNull(message = "El coreo  no puede estar vacio")
        @Email(message = "El formato es invalido .Intenta con un correo valido")
        @Size(max = 100, message = "Limite de caracteres alcanzado")
        String email
) {
}
