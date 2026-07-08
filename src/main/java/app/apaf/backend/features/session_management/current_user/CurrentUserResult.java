package app.apaf.backend.features.session_management.current_user;

import app.apaf.backend.domain.enums.UserStatus;

public record CurrentUserResult (
        Long idUser,
        String fullName,
        String email,
        String role,
        UserStatus status
){
}
