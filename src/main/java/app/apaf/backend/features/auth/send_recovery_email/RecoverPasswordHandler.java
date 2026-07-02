package app.apaf.backend.features.auth.send_recovery_email;


import app.apaf.backend.domain.email.EmailService;
import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.time.LocalDateTime.now;

/**
Generate UUID , send mail , reset attempts

Services recovery password (First part)
Second part is
@Link app.apaf.backend.features.auth.reset_password.ResetPasswordHandler

@Funtion This service get email, exclusive ACTIVE users status , and send an email
with recovery token , the recovery token is valid for 2 hours.
Unlock account if blocked ( many failed attempts ) and send an email with instructions for
update new password

@Author Uziel Abraham
@Version 1.0
 */

@Service
@RequiredArgsConstructor
public class RecoverPasswordHandler {

    // Recovery Password

    private final UserRepository userRepository;
    private  final EmailService emailService;


    @Transactional
    public RecoverPasswordResult recoverPassword(RecoverPasswordCommand recoverPasswordCommand)
    {

        userRepository.findByEmail(recoverPasswordCommand.email()).ifPresent(user -> {
            if (user.getStatus() == UserStatus.ACTIVO) {

                String recoveryToken = UUID.randomUUID().toString();
                user.setVerificationToken(recoveryToken);
                user.setExpirationToken(now().plusHours(2));
                user.setFailedAttempts(0);
                user.setAccountLocked(false);
                userRepository.save(user);

                emailService.sendRecoveryPasswordMail(user.getEmail(), recoveryToken, user.getFullName());
            }
        });
        return new RecoverPasswordResult("Email send successfully . Please follow the instructions");
    }

}
