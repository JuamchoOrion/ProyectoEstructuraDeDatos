package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.InteresRequest;
import com.redsocial.red_social.dto.UsuarioDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.UsuarioRepository;
import com.redsocial.red_social.service.UsuarioService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;
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
        response.put("rol", usuario.getClass().getSimpleName()); // Indica el tipo de usuario

        // Solo si es Estudiante, añadir intereses
        if (usuario instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usuario;
            if (!estudiante.getIntereses().isEmpty()) {
                response.put("intereses", estudiante.getIntereses().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()));
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @PostMapping("/{id}/intereses")
    public ResponseEntity<?> agregarInteres(
            @PathVariable Long id,
            @RequestBody InteresRequest request,
            Authentication authentication) {

        // Verificar que el usuario autenticado coincide con el ID
        if (!authentication.getName().equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para modificar este perfil");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        estudiante.getIntereses().add(request.getInteres());
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok("Interés agregado correctamente");
    }

    @DeleteMapping("/{id}/intereses")
    public ResponseEntity<?> eliminarInteres(
            @PathVariable Long id,
            @RequestBody InteresRequest request,
            Authentication authentication) {

        // Verificar que el usuario autenticado coincide con el ID
        if (!authentication.getName().equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para modificar este perfil");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        estudiante.getIntereses().remove(request.getInteres());
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok("Interés eliminado correctamente");
    }
    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar usuario: " + e.getMessage());
        }
}}
