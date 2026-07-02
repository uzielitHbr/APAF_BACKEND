package app.apaf.backend.features.auth.reset_password;

import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**

Services initialize recovery password (Second  part)

First part is in
@Link app.apaf.backend.features.auth.send_recovery_email.RecoverPasswordHandler

@Funtion This service gets the token and new password, validates expiration,
updates the password, and clears the tokens.

@Author Uziel Abraham
@Version 1.0
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public ResetPasswordResult forgotPassword(ResetPasswordCommand resetPasswordCommand) {

        User user = userRepository.findByVerificationToken(resetPasswordCommand.token())
                .orElseThrow(() -> new RuntimeException("Token invalid"));

        if (user.getStatus() != UserStatus.ACTIVO) {
            throw new RuntimeException("State invalid");
        }


        if (user.getExpirationToken() != null && LocalDateTime.now().isAfter(user.getExpirationToken())) {
            throw new RuntimeException("This token is expired");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordCommand.newPassword()));

        user.setVerificationToken(null);
        user.setExpirationToken(null);

        userRepository.save(user);



        return new ResetPasswordResult("Password update successfully ");
    }
}
