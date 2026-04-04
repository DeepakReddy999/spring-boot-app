package com.example.employeetaskmanagement.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    // This OpenAPI definition gives the Swagger UI a polished title, summary,
    // and Bearer JWT security instructions for local demos.
    @Bean
    public OpenAPI customerManagementOpenApi() {
        final String bearerSchemeName = "bearerAuth";

        return new OpenAPI()
            .info(
                new Info()
                    .title("Customer Management API")
                    .version("v1")
                    .description(
                        "REST API for customer management with JWT authentication. "
                            + "This local setup uses a shared-secret token flow for development and testing."
                    )
                    .contact(
                        new Contact()
                            .name("Engineering Demo Team")
                            .email("engineering-demo@company.com")
                    )
                    .license(new License().name("Internal Demo Use"))
            )
            .components(
                new Components().addSecuritySchemes(
                    bearerSchemeName,
                    new SecurityScheme()
                        .name(bearerSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Paste the JWT returned by POST /api/auth/login.")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList(bearerSchemeName));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
            .group("Authentication")
            .pathsToMatch("/api/auth/**")
            .build();
    }

    @Bean
    public GroupedOpenApi customerApi() {
        return GroupedOpenApi.builder()
            .group("Customers")
            .pathsToMatch("/customers/**", "/api/auth/**")
            .build();
    }
}
