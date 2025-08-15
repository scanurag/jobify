package com.org.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        
        // Allowed Origins
        config.addAllowedOrigin("https://jobify-1-3ow4.onrender.com");
        config.addAllowedOrigin("http://localhost:3000");
        
        // Allowed Headers & Methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // Apply CORS config to all endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
