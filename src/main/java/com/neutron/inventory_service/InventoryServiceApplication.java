package com.neutron.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    // Configuración global de CORS permitiendo cualquier origen
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")  // Rutas a las que se aplicará CORS
                        .allowedOrigins("*")  // Permite solicitudes desde cualquier origen
                        .allowedMethods("GET", "POST", "PUT", "DELETE")  // Métodos HTTP permitidos
                        .allowedHeaders("*");  // Permite todos los encabezados
            }
        };
    }
}
