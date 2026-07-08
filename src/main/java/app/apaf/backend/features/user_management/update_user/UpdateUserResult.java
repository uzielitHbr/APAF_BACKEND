package app.apaf.backend.features.user_management.update_user;

public record UpdateUserResult(
        Long idUser,
        String fullName,
        String phoneNumber,
        String newRole,
        String message
) {
}
