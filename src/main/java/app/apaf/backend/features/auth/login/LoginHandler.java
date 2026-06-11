package app.apaf.backend.features.auth.login;

import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.features.auth.login.input.LoginCommand;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler {

    private final UserRepository userRepository;


    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void login(LoginCommand loginCommand) {

    }
}
