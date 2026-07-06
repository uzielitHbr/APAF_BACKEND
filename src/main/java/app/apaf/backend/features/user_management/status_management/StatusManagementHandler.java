package app.apaf.backend.features.user_management.status_management;

import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatusManagementHandler {
    private  final UserRepository userRepository;

    @Transactional
    public StatusManagementResult updateStatus(Long idUser,StatusManagementCommand statusManagementCommand) {


        User user = userRepository.findById(idUser)
                .orElseThrow(()->new RuntimeException("user not found"));


        // Check if the user is self deactivation
        String notDeleteAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        if(user.getEmail().equalsIgnoreCase(notDeleteAdmin) && statusManagementCommand.updateStatus()== UserStatus.INACTIVO) {
            throw new RuntimeException("You cannot disable your own account");
        }
        //Check if NewStatus is the same as the old status
        if (user.getStatus()== statusManagementCommand.updateStatus()) {
            throw new RuntimeException("You cannot update the same status");
        }
        //Check if user doesnt have password (UserStatus PENDIENTE )
        if(user.getStatus()==UserStatus.PENDIENTE) {
            throw new RuntimeException("You cannot update this status. The users must activate their account");
        }

        if (statusManagementCommand.updateStatus() == UserStatus.PENDIENTE) {
            throw new RuntimeException("You cannot manually set a user's status to PENDIENTE.");
        }

        user.setStatus(statusManagementCommand.updateStatus());
        userRepository.save(user);

        return new StatusManagementResult("Status update successfully "+ " "+statusManagementCommand.updateStatus());
    }
}
