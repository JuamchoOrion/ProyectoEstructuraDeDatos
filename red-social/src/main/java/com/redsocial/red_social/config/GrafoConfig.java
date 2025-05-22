package com.redsocial.red_social.config;

import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrafoConfig {

    @Bean
    public GrafoEstudiantes grafoEstudiantes() {
        return new GrafoEstudiantes();
    }
}