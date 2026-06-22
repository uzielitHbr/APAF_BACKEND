package app.apaf.backend.domain.email;


import app.apaf.backend.domain.users.User;
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
    public void sendRecoveryPasswordMail(String email, String token, String nameUser) {

        String link = "http://localhost:5173/recover-password?token=" + token;
        //https://firebasestorage.googleapis.com/v0/b/apaf-40cf1.firebasestorage.app/o/APAF_email.png?alt=media&token=a42c05c1-c0d2-4733-925e-c63753796185
        String htmlFormat = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { background-color: #f4f4f4; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                    .wrapper { width: 100%%; background-color: #f4f4f4; padding: 20px 15px; box-sizing: border-box; }
                    .tarjeta { width: 100%%; max-width: 550px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); box-sizing: border-box; }
                    .cabecera { padding: 30px 30px 10px; border-top: 6px solid #14803c; text-align: left; }
                    .cuerpo { padding: 20px 30px 40px; color: #333333; }
                    .pie { text-align: center; padding: 20px; font-size: 12px; color: #999999; }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="tarjeta">
                        <div class="cabecera">
                            <img src="https://firebasestorage.googleapis.com/v0/b/apaf-40cf1.firebasestorage.app/o/APAF_email.png?alt=media&token=a42c05c1-c0d2-4733-925e-c63753796185" alt="Logo APAF" style="max-width: 500px; width: 100%%; height: auto; display: block;">
                        </div>
                        
                        <div class="cuerpo">
                            <h2 style="color: #14803c; margin-top: 0; font-size: 20 px;"> Hola, %s </h2>
                            <h2 style="color: #14803c; margin-top: 0; font-size: 20 px;">  recibimos tu solicitud:</h2>
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">
                                Se ha solicitado un restablecimiento de contraseña para tu cuenta en el sistema <strong>APAF</strong>. 
                                Para continuar y crear tu nueva contraseña, por favor haz clic en el siguiente botón:
                            </p>
                            
                            <div style="text-align: center; margin: 35px 0;">
                                <a href="%s" style="background-color: #14803c; color: #eaffd0; padding: 16px 36px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;">Restablecer contraseña</a>
                            </div>
                            <p style="color: #888888; font-size: 13px; line-height: 1.5; margin-bottom: 0;">
                                Por seguridad, este enlace <strong>expirará en 2 horas</strong>
                            </p>
                            
              
                            <p style="color: #888888; font-size: 13px; line-height: 1.5; margin-bottom: 0;">
                                Si no solicitaste este cambio, no te preocupes. Puedes ignorar este mensaje de forma segura y tu cuenta seguirá protegida.
                            </p>
                        </div>
                    </div>
                    
                    <div class="pie">
                        © 2026 APAF - Automatización de procesos para análisis financieros<br>
                        Este es un correo generado automáticamente.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nameUser ,link);


        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setFrom(emailHost);
            helper.setSubject("APAF - Recuperación de Contraseña");
            helper.setText(htmlFormat, true);


            mailSender.send(message);

            System.out.println("Email send successfully" + " "+link);
        } catch (MessagingException messagingException){
            System.out.println(messagingException.getMessage());

            System.out.println("Error"+messagingException.getMessage());
        }


    }

    // Set new password
    @Async
    public void sendPasswordSetup(String email, String token, String nameUser) {
        String link = "http://localhost:5173/setup-password?token=" + token;

        String htmlFormat = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { background-color: #f4f4f4; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                    .wrapper { width: 100%%; background-color: #f4f4f4; padding: 20px 15px; box-sizing: border-box; }
                    .tarjeta { width: 100%%; max-width: 550px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); box-sizing: border-box; }
                    .cabecera { padding: 30px 30px 10px; border-top: 6px solid #14803c; text-align: left; }
                    .cuerpo { padding: 20px 30px 40px; color: #333333; }
                    .pie { text-align: center; padding: 20px; font-size: 12px; color: #999999; }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="tarjeta">
                        <div class="cabecera">
                            <img src="https://firebasestorage.googleapis.com/v0/b/apaf-40cf1.firebasestorage.app/o/APAF_email.png?alt=media&token=a42c05c1-c0d2-4733-925e-c63753796185" alt="Logo APAF" style="max-width: 380px; width: 100%%; height: auto; display: block;">
                        </div>
                        
                        <div class="cuerpo">
                            <h2 style="color: #14803c; margin-top: 0; font-size: 22px;">¡Bienvenido a APAF, %s!</h2>
                            
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">
                                Se ha creado tu nueva cuenta en el sistema <strong>APAF</strong>. 
                                Para activar tu cuenta de forma segura y establecer tu contraseña por primera vez, por favor haz clic en el siguiente botón:
                            </p>
                            
                            <div style="text-align: center; margin: 35px 0;">
                                <a href="%s" style="background-color: #14803c; color: #eaffd0; padding: 16px 36px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;">Activar mi cuenta</a>
                            </div>
                            
                            <p style="color: #888888; font-size: 13px; line-height: 1.5; margin-bottom: 0;">
                                Por seguridad, este enlace de activación <strong>expirará en 3 horas</strong>. Si el tiempo se agota, deberás pedirle al administrador que te reenvíe la invitación.
                            </p>
                        </div>
                    </div>
                    
                    <div class="pie">
                        © 2026 APAF - Automatización de procesos para análisis financieros<br>
                        Este es un correo generado automáticamente.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nameUser, link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setFrom(emailHost);
            helper.setSubject("APAF - ¡Bienvenido! Activa tu cuenta");
            helper.setText(htmlFormat, true);

            mailSender.send(message);
            System.out.println("Email de bienvenida enviado exitosamente: " + link);

        } catch (MessagingException messagingException) {
            System.out.println(messagingException.getMessage());
            System.out.println("Error enviando correo de bienvenida: " + messagingException.getMessage());
        }
    }




}