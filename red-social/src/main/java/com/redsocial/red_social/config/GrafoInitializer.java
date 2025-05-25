package com.redsocial.red_social.config;

import com.redsocial.red_social.service.GrafoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrafoInitializer implements ApplicationRunner {

    private final GrafoService grafoService;

    public GrafoInitializer(GrafoService grafoService) {
        this.grafoService = grafoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        grafoService.inicializarGrafoTransactional(); // Aquí ya funciona @Transactional
    }
}
