package app.apaf.backend.features.auth.login.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginCommand(
        @NotBlank(message = "Este campo no puede estar vacio")
        @Email(message = "El formato es invalido .Intenta con un correo valido")
        @Size(max = 100, message = "Limite de caracteres alcanzado")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 64, message = "La contraseña no puede establcerse con esos caracteres")
        String password
) {
}
