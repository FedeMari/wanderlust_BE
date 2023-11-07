package com.darkorbit.wanderlust.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
    
    /*	Se desideri inviare i cookie con le richieste CORS, devi impostare 
     * allowCredentials su true e assicurarti che il server risponda con 
     * l'intestazione Access-Control-Allow-Credentials impostata su true. 
     * Questo indica al browser che il server accetta richieste CORS con 
     * credenziali e consente l'invio dei cookie.
     */
    
}
