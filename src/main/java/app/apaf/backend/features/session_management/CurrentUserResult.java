package app.apaf.backend.features.session_management;

import app.apaf.backend.domain.enums.UserStatus;

public record CurrentUserResult (
        Long idUser,
        String fullName,
        String email,
        String role,
        UserStatus status
){
}
