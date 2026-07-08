package app.apaf.backend.features.session_management.current_user;

import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * This service gets the JWT and obtains the user's session information
 @Author Uziel Abraham
 @Version 1.0

 */
@Service
@RequiredArgsConstructor
public class CurrentUserQueryHandler {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CurrentUserResult currentUsers() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new CurrentUserResult(
                user.getIdUser(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getCodeRole(),
                user.getStatus()
        );
    }
}
