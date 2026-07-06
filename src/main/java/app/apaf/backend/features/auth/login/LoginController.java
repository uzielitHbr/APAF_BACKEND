package app.apaf.backend.features.auth.login;


import app.apaf.backend.features.auth.login.input.LoginCommand;
import app.apaf.backend.features.auth.login.output.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")

@RequiredArgsConstructor

@Tag(name = "Autenticación y Seguridad", description = "Endpoints públicos para el acceso y recuperación de cuentas")
public class LoginController {

    private final LoginHandler loginHandler;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión. Generar JWT",
            description = "Autentica al usuario mediante correo y contraseña. Devuelve el Token JWT necesario para acceder a las rutas protegidas del sistema. Bloquea la cuenta tras 3 intentos fallidos."
    )
    public ResponseEntity<?> login(@Valid  @RequestBody LoginCommand loginCommand) {
        try{
            LoginResult loginResult = loginHandler.login(loginCommand);
            return ResponseEntity.ok(loginResult);

         } catch (LockedException lockedException){
            //return 423 Locked
            return ResponseEntity.status(HttpStatus.LOCKED).body(lockedException.getMessage());
        }
        catch(BadCredentialsException badCredentialsException){
            // return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(badCredentialsException.getMessage());
        } catch (Exception ex ){
            //return 500 Internal Server
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");

        }

    }

}
