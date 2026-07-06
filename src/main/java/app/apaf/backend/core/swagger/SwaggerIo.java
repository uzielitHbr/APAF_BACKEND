package app.apaf.backend.core.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info (
                title = "API documentation for Federación Regional de Cooperativa de Ahorro y Prestamo Centro-sur",
                version = "1.0",
                description = "These are the endpoints for APAF"


        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "aqui va algo pero pintalo",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerIo {
}
