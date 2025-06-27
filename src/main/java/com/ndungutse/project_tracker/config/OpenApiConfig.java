package com.ndungutse.project_tracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${server.port}")
        private String serverPort;

        @Bean
        public OpenAPI projectTrackerOpenAPI() {
                Server localServer = new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server");

                Contact contact = new Contact()
                                .name("Project Tracker Team")
                                .email("dav.ndungutse@gmail.com");

                Info info = new Info()
                                .title("Project Tracker API")
                                .description("RESTful API for managing projects, tasks, and developers. Requires JWT authentication for protected endpoints.")
                                .version("1.0.0")
                                .contact(contact);

                // Define the security scheme for JWT
                SecurityScheme securityScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Enter your JWT token in the format: Bearer <token>");

                // Add security scheme to components
                Components components = new Components()
                                .addSecuritySchemes("bearerAuth", securityScheme);

                // Add security requirement to all endpoints
                SecurityRequirement securityRequirement = new SecurityRequirement()
                                .addList("bearerAuth");

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(localServer))
                                .components(components)
                                .addSecurityItem(securityRequirement);
        }
}