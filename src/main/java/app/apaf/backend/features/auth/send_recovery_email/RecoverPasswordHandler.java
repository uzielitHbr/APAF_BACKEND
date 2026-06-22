package app.apaf.backend.features.auth.send_recovery_email;


import app.apaf.backend.domain.email.EmailService;
import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalDateTime.now;

/*
Generate UUID , send mail , reset attempts

This service get email, exclusive ACTIVE users status , and send an email
with recovery token , the recovery token is valid for 2 hours

@Uziel Abraham
@Version 1.0
 */

@Service
@AllArgsConstructor
public class RecoverPasswordHandler {

    // Recovery Password

    private final UserRepository userRepository;
    private  final EmailService emailService;

    public String recoverPassword(RecoverPasswordCommand recoverPasswordCommand)
    {

        userRepository.findByEmail(recoverPasswordCommand.email()).ifPresent(user -> {
            if (user.getStatus() == UserStatus.ACTIVO) {

                String recoveryToken = UUID.randomUUID().toString();
                user.setVerificationToken(recoveryToken);
                user.setExpirationToken(now().plusHours(2));
                userRepository.save(user);

                emailService.sendRecoveryPasswordMail(user.getEmail(), recoveryToken, user.getFullName());
            }
        });
        return "Email send successfully . Please follow the instructions";
    }

}
