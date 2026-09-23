package sn.sdley.springbootstarter0.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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
public class OpenApiConfig {
}
