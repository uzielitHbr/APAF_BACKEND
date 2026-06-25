package app.apaf.backend.features.auth.send_recovery_email;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/recover-password")
@RequiredArgsConstructor
public class RecoverPasswordController {

    private final RecoverPasswordHandler recoverPasswordHandler;

    @PostMapping
    public ResponseEntity<String> recoverPassword(@Valid @RequestBody RecoverPasswordCommand command) {

        String resultMessage = recoverPasswordHandler.recoverPassword(command);

        return ResponseEntity.ok(resultMessage);
    }
}