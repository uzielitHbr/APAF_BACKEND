package app.apaf.backend.features.auth.login.input;

public record LoginCommand(
        String email,
        String password
) {
}
