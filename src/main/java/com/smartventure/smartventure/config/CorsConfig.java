package com.smartventure.smartventure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        /* Замените localhost:3000 на адрес вашего фронтенда */
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://your-frontend.com"));
        config.setAllowedMethods(List.of("*"));    // GET, POST, PUT, DELETE...
        config.setAllowedHeaders(List.of("*"));    // все заголовки
        config.setAllowCredentials(true);
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}