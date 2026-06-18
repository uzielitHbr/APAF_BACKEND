package app.apaf.backend.features.auth.set_password;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class sendSetUpEmail {

    //New User
    @Async
    public void sendPasswordSetUpEmail(String email, String token) {

        String mail = "http://localhost:5173/setup-password?token=" + token;


    }

    // Recovery Password
    @Async
    public void sendPasswordResetEmail(String email, String token) {


    }


}
