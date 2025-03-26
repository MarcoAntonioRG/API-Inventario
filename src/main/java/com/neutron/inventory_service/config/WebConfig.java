package com.neutron.inventory_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esta configuración sirve archivos estáticos desde /app/images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/app/images/");
    }
}
