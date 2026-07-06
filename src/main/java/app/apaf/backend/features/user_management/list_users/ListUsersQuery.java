package app.apaf.backend.features.user_management.list_users;

import app.apaf.backend.domain.enums.UserStatus;

import java.util.List;

public record ListUsersQuery(
        Long idUser,
        String fullname,
        String phoneNumber,
        String email,
        String role,
        UserStatus status

) {
}
