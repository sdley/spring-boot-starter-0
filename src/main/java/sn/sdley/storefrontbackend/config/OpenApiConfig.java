package sn.sdley.storefrontbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Storefront API",
                version = "v1",
                description = """
                        A modern REST API for powering seamless storefront experiences.

                        Manage users, products, and shopping carts through clear, predictable endpoints
                        designed for fast integration and dependable commerce workflows.
                        """
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Access token returned by the authentication endpoints."
)
public class OpenApiConfig {
}
