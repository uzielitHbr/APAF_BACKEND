package app.apaf.backend.features.user_management.status_management;

import app.apaf.backend.domain.enums.UserStatus;

import jakarta.validation.constraints.NotNull;

public record StatusManagementCommand (
        UserStatus updateStatus
){
}
