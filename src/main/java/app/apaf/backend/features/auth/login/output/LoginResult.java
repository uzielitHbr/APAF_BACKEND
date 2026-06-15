package app.apaf.backend.features.auth.login.output;

import app.apaf.backend.domain.enums.UserStatus;

import java.util.List;

public record LoginResult(
    String tokenJWT,
    String fullName,
    UserStatus status,
    String email,
    List<String> permisos

){

}
