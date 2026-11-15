package com.cibermax.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ciber Max - Ecommerce API",
                version = "1.0.0",
                description = "API REST del sistema de comercio electrónico Ciber Max"
        )
)
public class OpenApiConfig {
}
