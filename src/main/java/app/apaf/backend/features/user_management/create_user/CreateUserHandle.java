package app.apaf.backend.features.user_management.create_user;



import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*
This service it will create a user , this option is available only role Admin
Input FullName, email , phoneNumber , Role
it will return a http request 200 OK and , it'll send a mail

@Uziel Abraham
@Version 1.0

 */
@Service
@AllArgsConstructor
public class CreateUserHandle {

    private UserRepository userRepository;


    @Transactional
    public String createUser(CreateUserCommand createUserCommand) {

        // We extrect id´s admin from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long createdBy = Long.parseLong(authentication.getName());

        User createdById = userRepository.getReferenceById(createdBy);

        User user = new User();

        user.setCreatedBy(createdById);
        user.setFullName(createUserCommand.fullName());
        user.setEmail(createUserCommand.email());
        user.setPhoneNumber(createUserCommand.phoneNumber());
        user.setStatus(UserStatus.PENDENTE);

        userRepository.save(user);

        //Send mail ( We'll create )

        return "User created successfully";
    }


}
