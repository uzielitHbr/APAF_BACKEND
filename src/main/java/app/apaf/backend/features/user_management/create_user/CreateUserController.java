package app.apaf.backend.features.user_management.create_user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/users/create")
@RequiredArgsConstructor
public class CreateUserController {
    private  final CreateUserHandler createUserHandler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CreateUserResult> createUser(@Valid @RequestBody CreateUserCommand command) {
        System.out.println("User created by : ");
        CreateUserResult response = createUserHandler.createUser(command);
        return ResponseEntity.ok(response);
    }

}
