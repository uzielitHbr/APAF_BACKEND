package app.apaf.backend.domain.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

/*
This service it'll be attendant create Email format
and send a link
@Uziel Abraham
@Version 1.0
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value ("${EMAIL_HOST}")
    private String emailHost;


    // Recover Password
    @Async
    public void sendPasswordResetEmail(String email, String token) {
        String link = "http://localhost:5173/reset-password?token=" + token;
        String htmlFormat ="""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <style>
                    .boton { background-color: #000000; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block; font-weight: bold;}
                    .caja { border: 1px solid #eaeaea; padding: 30px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 500px; margin: auto; border-radius: 8px;}
                </style>
            </head>
            <body>
                <div class="caja">
                    <h2 style="color: #333;">Recuperación de contraseña de APAF</h2>
                    <p style="color: #555;">Hola, hemos recibido una solicitud para restablecer tu acceso al sistema.</p>
                    <br>
                    <a href="%s" class="boton">Cambiar mi contraseña</a>
                    <br><br>
                    <p style="color: #888; font-size: 12px;">Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.</p>
                </div>
            </body>
            </html>
            """.formatted(link);

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setFrom(emailHost);
            helper.setSubject("APAF - Recuperación de Contraseña");
            helper.setText(htmlFormat, true);


            mailSender.send(message);

            System.out.println("Email send successfully" + link);
        } catch (MessagingException messagingException){
            System.out.println(messagingException.getMessage());

            System.out.println("Error"+messagingException.getMessage());
        }


    }

    // Set new password
    @Async
    public void sendPasswordSetup(String email, String token) {
        String link = "http://localhost:5173/setup-password?token=" + token;

        String htmlFormat =
                "<!DOCTYPE html>\n " +" ".formatted(link);




    }
}