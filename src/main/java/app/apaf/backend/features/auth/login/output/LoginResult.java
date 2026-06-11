package app.apaf.backend.features.auth.login.output;

import java.util.List;

public record LoginResult(

    String tokenJWT,
    String fullName,
    String email,
    List<String> permisos

){

}
