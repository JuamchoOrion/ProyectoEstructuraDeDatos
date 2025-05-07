package com.redsocial.red_social.controller;

import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> getPerfilUsuario(
            @RequestHeader("Authorization") String token) {

        // Extraer username del token
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));

        // Buscar usuario en BD
// En tu método
        Usuario usuario = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("username", usuario.getUsername());
        response.put("email", usuario.getEmail());


        return ResponseEntity.ok(response);
    }
}