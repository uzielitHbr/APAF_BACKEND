package app.apaf.backend.domain.email;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
This service it'll be attendant create Email format
and send a link
@Uziel Abraham
@Version 1.0
 */
@Service
public class EmailService {


    // Recover Password
    @Async
    public void sendPasswordResetEmail(String email, String token) {
        String link = "http://localhost:5173/reset-password?token=" + token;
        String htmlFormat =
                "<!DOCTYPE html>\n " +" ".formatted(link);

        System.out.println("Enviando correo a: " + email + " con link: " + link);
    }

    // Set new password
    @Async
    public void sendPasswordSetup(String email, String token) {
        String link = "http://localhost:5173/setup-password?token=" + token;

        String htmlFormat =
                "<!DOCTYPE html>\n " +" ".formatted(link);




    }
}