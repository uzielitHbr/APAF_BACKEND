package app.apaf.backend.features.auth.set_password_newUsers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/set-password")
@RequiredArgsConstructor
public class SetPasswordController {

    private final SetPasswordHandler setPasswordHandler;

    @PatchMapping
    public ResponseEntity<SetPasswordResult> setPassword(@Valid @RequestBody SetPasswordCommand command) {
        SetPasswordResult result = setPasswordHandler.newPassword(command);
        return ResponseEntity.ok(result);
    }

}
