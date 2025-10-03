package hexlet.code.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0.0")
                        .description("REST API to manage users, tasks, and task statuses")
                        .contact(new Contact()
                                .name("Irina Komarchenko")
                                .email("irinakomarchenko@example.com")
                                .url("https://github.com/irinakomarchenko"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                );
    }
}
