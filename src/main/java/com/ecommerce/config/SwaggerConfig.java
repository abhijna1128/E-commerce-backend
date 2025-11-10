package com.ecommerce.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "BearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT"
)
public class SwaggerConfig {
}
