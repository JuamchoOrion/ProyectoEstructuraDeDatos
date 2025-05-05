package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.RegistroDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistroRestController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroDTO datos) {

        if (!datos.getPassword().equals(datos.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }

        if (estudianteRepository.findByEmail(datos.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        Estudiante estudiante = new Estudiante(datos.getNombre(), datos.getEmail(), datos.getPassword());
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok("Registro exitoso");
    }
}
