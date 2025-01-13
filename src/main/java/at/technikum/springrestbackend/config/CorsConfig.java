package at.technikum.springrestbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow requests to all endpoints
                        .allowedOrigins("http://localhost:8081", "http://localhost:8080") // Frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true); // Allow cookies/authorization headers
            }
        };
    }
}
