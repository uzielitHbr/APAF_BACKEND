package app.apaf.backend.features.auth.login;

import app.apaf.backend.core.security.JwtService;
import app.apaf.backend.domain.enums.RoleUser;
import app.apaf.backend.domain.users.Role;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.features.auth.login.input.LoginCommand;
import app.apaf.backend.features.auth.login.output.LoginResult;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;

import static java.time.LocalDateTime.now;


/*
@Version 1.0
 */
@Service
@RequiredArgsConstructor
public class LoginHandler {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(noRollbackFor = {
            BadCredentialsException.class,
            LockedException.class
    })

    public LoginResult login(LoginCommand loginCommand) {
        try{
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginCommand.email(), loginCommand.password())
                );
        }catch (LockedException lockedException) {
            throw new LockedException("Account is locked");
        } catch (BadCredentialsException credentialsException) {
            boolean lockedAccount = failAttemps(loginCommand.email());
            if (lockedAccount) {
                throw new LockedException("Account locked after too many failed attempts.Try again after 3 minutes");
            }
            throw new BadCredentialsException("Invalid email or password");
        }


        User user = userRepository.findByEmail(loginCommand.email())
                .orElseThrow(()->new RuntimeException("User not found"));

            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);

        String token = jwtService.generateJwtToken(user.getIdUser(), user.getRole().name());

        return new LoginResult(
                token,
                user.getFullName(),
                user.getStatus(),
                user.getEmail(),
                user.getRole().name()
        );
    }


    private boolean failAttemps(String mail) {
        User user = userRepository.findByEmail(mail).orElse(null);
        if (user != null) {


            if (user.getLockTime() != null && now().isAfter(user.getLockTime().plusMinutes(3))) {
                user.setFailedAttempts(0);
                user.setLockTime(null);
            }
            int attemps = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
            user.setFailedAttempts(attemps + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setLockTime(now());
                userRepository.save(user);
                return true;
            }
            userRepository.save(user);
        }
        return false;
    }

}
