package app.apaf.backend.features.user_management.resend_setup_email;


import app.apaf.backend.domain.email.EmailService;
import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


/*
This service it will resend an email invitation excusive if new users get status PENDIENTE
 */
@Service
@RequiredArgsConstructor
public class ResendEmailHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public ResendEmailResult resendSetUpEmail(ResendEmailCommand resendEmailCommand) {

        User user = userRepository.findByEmail(resendEmailCommand.email())
                .orElseThrow( ()-> new RuntimeException("User not found!"));
        if (user.getStatus()!= UserStatus.PENDIENTE)
        {
            throw new RuntimeException("Status error. Contact administrator");
        }

        String setupToken = UUID.randomUUID().toString();
        user.setVerificationToken(setupToken);
        user.setExpirationToken(LocalDateTime.now().plusHours(6));

        emailService.sendPasswordSetup(user.getEmail(), setupToken, user.getFullName());



        return new ResendEmailResult("Email has been resent successfully");
    }


}
