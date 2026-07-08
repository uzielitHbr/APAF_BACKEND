package app.apaf.backend.features.session_management.update_password;


import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePasswordHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UpdatePasswordResult updatePasswordResult(UpdatePasswordCommand updatePasswordCommand) {


        if (!updatePasswordCommand.newPassword().equals(updatePasswordCommand.confirmNewPassword())) {
            throw new RuntimeException("The passwords don't match");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (passwordEncoder.matches(updatePasswordCommand.newPassword(), user.getPassword())) {
            throw new RuntimeException("New password must not be the same");
        }


        user.setPassword(passwordEncoder.encode(updatePasswordCommand.newPassword()));
        userRepository.save(user);

        return new UpdatePasswordResult("New Password Updated successfully");
    }





}
