package com.tuition.tms.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tuitionManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tuition Management System - REST API")
                        .description("REST API documentation for Student Management Module (Person 1) in Tuition Management System")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@tuitionmanagement.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
