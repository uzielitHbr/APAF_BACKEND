package app.apaf.backend.features.user_management.create_user;


import app.apaf.backend.domain.users.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserCommand (

        @NotNull(message = "El nombre no puede estar vacio")
        String fullName,
        @NotNull(message = "El correo no puede estar vacio ")
        @Email(message = "El formato es invalido .Intenta con un correo válido")
        String email,
        @NotNull(message = "El telefono no puede estar vacio")
        @Size(min = 10, max = 10)
        @Pattern(regexp = "^[0-9]+$", message = "Solo se permiten números")
        String phoneNumber ,
        @NotNull(message = "Este campo no puede estar vacio")
        Role role

){
}
