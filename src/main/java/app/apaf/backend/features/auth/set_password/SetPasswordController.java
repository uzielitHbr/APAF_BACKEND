package app.apaf.backend.features.auth.set_password;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/set-password")
@RequiredArgsConstructor
public class SetPasswordController {

    private final SetPasswordHandler setPasswordHandler;

    @PatchMapping
    public ResponseEntity<String> setPassword(@Valid @RequestBody SetPasswordCommand command) {
        String message = setPasswordHandler.execute(command);
        return ResponseEntity.ok(message);
    }

}
