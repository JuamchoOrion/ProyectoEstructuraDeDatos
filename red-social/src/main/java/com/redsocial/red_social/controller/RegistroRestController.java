package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.RegistroDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.repository.EstudianteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RegistroRestController {

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Añade esto


    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO datos) {
        try {
            // Validaciones
            if (!datos.getPassword().equals(datos.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Las contraseñas no coinciden")
                );
            }

            if (estudianteRepository.findByUsername(datos.getNombre()).isPresent()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "El usuario ya está registrado")
                );
            }

            // Crear estudiante
            Estudiante estudiante = new Estudiante();
            estudiante.setUsername(datos.getNombre());
            estudiante.setEmail(datos.getEmail());
            estudiante.setPassword(passwordEncoder.encode(datos.getPassword()));

            // Convertir intereses a enum
            // Asignar intereses directamente (ya vienen como Enum)
            if (datos.getIntereses() != null) {
                estudiante.setIntereses(new HashSet<>(datos.getIntereses()));
            }


            estudianteRepository.save(estudiante);

            return ResponseEntity.ok(Map.of(
                    "message", "Registro exitoso",
                    "id", estudiante.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Uno o más intereses no son válidos")
            );
        }
    }
}
