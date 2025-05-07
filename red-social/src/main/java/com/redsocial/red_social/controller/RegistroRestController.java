package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.RegistroDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistroRestController {

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Añade esto


    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroDTO datos) {

        if (!datos.getPassword().equals(datos.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }

        if (estudianteRepository.findByUsername(datos.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya está registrado");
        }

        Estudiante estudiante = new Estudiante(
                datos.getNombre(),
                datos.getEmail(),
                passwordEncoder.encode(datos.getPassword()) // Codifica la contraseña
        );
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok("Registro exitoso");
    }
}
