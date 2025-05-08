package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.RegistroModeradorDTO;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.service.ModeradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderadores")
public class RegistroModeradorController {

    private final ModeradorService moderadorService;

    @Autowired
    public RegistroModeradorController(ModeradorService moderadorService) {
        this.moderadorService = moderadorService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarModerador(@RequestBody RegistroModeradorDTO dto) {
        try {
            Moderador nuevoModerador = moderadorService.registrarModerador(dto);
            return ResponseEntity.ok("Moderador registrado exitosamente: " + nuevoModerador.getUsername());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}