package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.EstudianteDTO;
import com.redsocial.red_social.dto.InteresRequest;
import com.redsocial.red_social.dto.UsuarioDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.UsuarioRepository;
import com.redsocial.red_social.service.EstudianteService;
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
    private EstudianteService estudianteService;
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
    }

    @GetMapping("/{id}/verificarRol")
    public ResponseEntity<?> verificarRolUsuario(@PathVariable Long id) {
        try {
            String rol = usuarioService.obtenerRolUsuario(id);
            return ResponseEntity.ok().body(Map.of("rol", rol));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al verificar rol: " + e.getMessage());
        }
    }

    // Endpoint para agregar amigo
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAmigo(
            @RequestHeader("Authorization") String token,
            @RequestParam String userAmigo) {

        try {
            // Obtener usuario actual del token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Estudiante estudianteActual = estudianteService.buscarPorUsername(username);
            Estudiante amigo = estudianteService.buscarPorUsername(userAmigo);
            // Llamar al servicio para agregar amigo
            estudianteService.agregarAmigo(estudianteActual.getId(), amigo.getId());
            return ResponseEntity.ok().build();

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint para eliminar amigo
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarAmigo(
            @RequestHeader("Authorization") String token,
            @RequestParam Long idAmigo) {

        try {
            // Obtener usuario actual del token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Estudiante estudianteActual = estudianteService.buscarPorUsername(username);

            // Llamar al servicio para eliminar amigo
            estudianteService.eliminarAmigo(estudianteActual.getId(), idAmigo);
            return ResponseEntity.ok().build();

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Estudiante estudiante = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(Map.of("id", estudiante.getId(), "username", estudiante.getUsername()));
    }

    @GetMapping("/amigos")
    public ResponseEntity<?> getAmigos(Authentication authentication) {
        String username = authentication.getName();
        Estudiante estudiante = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Map<String, Object>> amigos = estudiante.getAmigos().stream().map(amigo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", amigo.getId());
            map.put("nombre", amigo.getUsername()); // o getUsername(), si prefieres
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(amigos);
    }
    @GetMapping("/cargarDatos/{id}")
    public ResponseEntity<?> cargarDatosEstudiante(@PathVariable Long id) {
        try {
            Estudiante estudiante = estudianteService.obtenerPorId(id);
            EstudianteDTO dto = convertirAEstudianteDTO(estudiante);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private EstudianteDTO convertirAEstudianteDTO(Estudiante estudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setId(estudiante.getId());
        dto.setUsername(estudiante.getUsername());
        dto.setEmail(estudiante.getEmail());
        dto.setPassword(estudiante.getPassword());
        dto.setIntereses(estudiante.getIntereses());
        return dto;
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> actualizarEstudiante(
            @PathVariable Long id,
            @RequestBody EstudianteDTO estudianteDTO) {
        try {
            Estudiante estudianteActualizado = estudianteService.actualizar(id, estudianteDTO);
            return ResponseEntity.ok(estudianteActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
}