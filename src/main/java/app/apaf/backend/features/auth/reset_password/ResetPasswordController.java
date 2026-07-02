package app.apaf.backend.features.auth.reset_password;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reset-password")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordHandler  resetPasswordHandler;

    @PatchMapping
    public ResponseEntity<ResetPasswordResult> resetPassword(
           @Valid @RequestBody ResetPasswordCommand resetPasswordCommand
    ){
        ResetPasswordResult result = resetPasswordHandler.forgotPassword(resetPasswordCommand);
        return ResponseEntity.ok(result);
    }


}
