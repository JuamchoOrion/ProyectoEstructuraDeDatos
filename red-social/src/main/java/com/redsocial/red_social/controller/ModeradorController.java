package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.RegistroDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/api/moderadores")
public class ModeradorController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtTokenProvider;

    @PostMapping("/crear-estudiante")
    public ResponseEntity<?> crearEstudiante(
            @Valid @RequestBody RegistroDTO datos,
            @RequestHeader("Authorization") String authHeader) { // Obtener token del moderador

        // Extraer username del moderador desde el token
        String token = authHeader.substring(7); // Eliminar "Bearer "
        String usernameModerador = jwtTokenProvider.extractUsername(token);

        if (estudianteRepository.findByUsername(datos.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "El usuario ya está registrado")
            );
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setUsername(datos.getNombre());
        estudiante.setEmail(datos.getEmail());
        estudiante.setPassword(passwordEncoder.encode(datos.getPassword()));

        if (datos.getIntereses() != null) {
            estudiante.setIntereses(new HashSet<>(datos.getIntereses()));
        }

        estudianteRepository.save(estudiante);

        // Devolver el mismo token del moderador (o puedes refrescarlo si lo prefieres)
        return ResponseEntity.ok(Map.of(
                "message", "Estudiante creado correctamente por " + usernameModerador,
                "id", estudiante.getId(),
                "token", token, // Devolvemos el mismo token
                "username", usernameModerador // Opcional: devolver el username del moderador
        ));
    }
}