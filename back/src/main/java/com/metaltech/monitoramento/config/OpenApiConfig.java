package com.metaltech.monitoramento.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Documentação da API (Swagger UI em /swagger-ui.html).
 * O botão "Authorize" usa o mesmo login HTTP Basic da API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MetalTech - API de Monitoramento Industrial",
                version = "v1",
                description = "API REST que integra a esteira de triagem (automação), "
                        + "a análise de dados, o dashboard web e a aplicação de Realidade Aumentada."
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class OpenApiConfig {
}