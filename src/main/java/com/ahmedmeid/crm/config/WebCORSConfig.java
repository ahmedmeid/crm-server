package com.ahmedmeid.crm.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Setter
@Component
@Configuration
public class WebCORSConfig implements WebMvcConfigurer {

    @Value("${security.allowed-client-address}")
    private String allowedClientAddress;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            .allowedOrigins(allowedClientAddress)
            .allowCredentials(true);
    }
}
