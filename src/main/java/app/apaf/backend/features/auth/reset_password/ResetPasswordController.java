package app.apaf.backend.features.auth.reset_password;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reset-password")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordHandler  resetPasswordHandler;

    @PatchMapping
    public ResponseEntity<ResetPasswordResult> resetPassword(
            ResetPasswordCommand resetPasswordCommand
    ){
        ResetPasswordResult result = resetPasswordHandler.forgotPassword(resetPasswordCommand);
        return ResponseEntity.ok(result);
    }


}
