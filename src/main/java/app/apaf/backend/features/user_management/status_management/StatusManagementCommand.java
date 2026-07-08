package app.apaf.backend.features.user_management.status_management;

import app.apaf.backend.domain.enums.UserStatus;

import jakarta.validation.constraints.NotNull;

public record StatusManagementCommand (
        @NotNull(message = "El nuevo estado no puede estar vacío")
        UserStatus updateStatus
){
}
