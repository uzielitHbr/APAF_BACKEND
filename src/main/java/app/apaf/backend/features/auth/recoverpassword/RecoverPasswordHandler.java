package app.apaf.backend.features.auth.recoverpassword;


import app.apaf.backend.domain.email.EmailService;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

/*
Generate UUID , send mail , reset attempts

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
            String recoveryToken = UUID.randomUUID().toString();
            user.setRecoveryToken(recoveryToken);
            userRepository.save(user);
            emailService.sendRecoveryPasswordMail(user.getEmail(), recoveryToken);
        });
        return "Email send successfully . Please follow the instructions";
    }

}
