package app.apaf.backend.features.user_management.create_user;



import app.apaf.backend.domain.email.EmailService;
import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.Role;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.domain.users.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


/*
This service it will create a user , this option is available only role Admin
Input FullName, email , phoneNumber , Role
it will return an http request 200 OK and , it'll send a mail

@Uziel Abraham
@Version 1.0

 */
@Service
@AllArgsConstructor
public class CreateUserHandler {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private EmailService emailService;

    @Transactional
    public CreateUserResult createUser(CreateUserCommand createUserCommand) {

        // We extrect id´s admin from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String createdByMail = authentication.getName();

        User createdByUser = userRepository.findByEmail(createdByMail)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Role rol = roleRepository.findByCodeRole(createUserCommand.role())
                .orElseThrow(() -> new RuntimeException("Role Not Found"));
        User user = new User();

        user.setCreatedBy(createdByUser);
        user.setFullName(createUserCommand.fullName());
        user.setEmail(createUserCommand.email());
        user.setPhoneNumber(createUserCommand.phoneNumber());
        user.setRole(rol);
        user.setStatus(UserStatus.PENDIENTE);

        //Send mail ( We'll create ) New User

        String setupToken = UUID.randomUUID().toString();
        user.setVerificationToken(setupToken);
        user.setExpirationToken(LocalDateTime.now().plusHours(6));

        userRepository.save(user);


        emailService.sendPasswordSetup(user.getEmail(), setupToken, user.getFullName());

        return new CreateUserResult("User created successfully");
    }


}
