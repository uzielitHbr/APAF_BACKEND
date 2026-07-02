package app.apaf.backend.features.auth.set_password_newUsers;



import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.time.LocalTime.now;

/*
Exclusive service for new users , and status PENDIENTE ,
admin created with a 6 hours expiration , required token and newPassword

@Uziel Abraham
@Version 1.0
 */
@Service
@RequiredArgsConstructor
public class SetPasswordHandler {


    //New User

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SetPasswordResult newPassword(SetPasswordCommand command) {

        User user = userRepository.findByVerificationToken(command.token())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStatus() != UserStatus.PENDIENTE) {
            throw new RuntimeException("Status error. Contact administrator");
        }

        if (user.getExpirationToken() != null && LocalDateTime.now().isAfter(user.getExpirationToken())) {
            throw new RuntimeException("Token expired. Contact administrator");
        }

        user.setPassword(passwordEncoder.encode(command.newPassword()));

        user.setStatus(UserStatus.ACTIVO);


        user.setVerificationToken(null);
        user.setExpirationToken(null);

        userRepository.save(user);

        return new SetPasswordResult("Account activated successfully");
    }


}
