package com.example.holidayplanner.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI().info(new Info().title("GetAway")
                .description("Api documentation for GetAway")
                .version("v0.01")
                .license(new License()
                        .name("Apache 2.0").url("http://springdoc.org")
                ))
                .components(new Components().addSecuritySchemes("Api Key", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .scheme("bearer")
                        .bearerFormat("jwt")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                )).addSecurityItem(new SecurityRequirement().addList("Api Key"));
    }
}
