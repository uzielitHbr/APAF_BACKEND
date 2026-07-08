package app.apaf.backend.features.user_management.update_user;

import app.apaf.backend.domain.users.Role;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.RoleRepository;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Validates security rules, updates the user entity, and saves to DB
 * @Author Uziel Abraham
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UpdateUserHandler {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public UpdateUserResult updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.idUser())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + command.idUser()));

        Role newRole = roleRepository.findById(command.idRole())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + command.idRole()));

        // Logged-in admin cannot change their own role
        String loggedAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getEmail().equalsIgnoreCase(loggedAdminEmail) && !user.getRole().getIdRole().equals(newRole.getIdRole())) {
            throw new RuntimeException("An Administrator cannot modify their own role.");
        }

        user.setFullName(command.fullName());
        user.setPhoneNumber(command.phoneNumber());
        user.setRole(newRole);

        userRepository.save(user);

        return new UpdateUserResult(
                user.getIdUser(),
                user.getFullName(),
                user.getPhoneNumber(),
                newRole.getCodeRole(),
                "User profile and role updated successfully."
        );
    }
}
