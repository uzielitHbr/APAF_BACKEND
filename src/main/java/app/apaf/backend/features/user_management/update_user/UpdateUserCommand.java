package app.apaf.backend.features.user_management.update_user;

/**
 * Combines the target user ID from the URL and the update data from the body.
 */
public record UpdateUserCommand(
        Long idUser,
        String fullName,
        String phoneNumber,
        Long idRole
) {
}
