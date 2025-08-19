package com.marketplace.marketplace_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Marketplace Backend API",
                version = "1.0",
                description = "Documentação da API do Marketplace"
        )
)
public class OpenApiConfig {
}
