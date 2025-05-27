package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.ContenidoDTO;
import com.redsocial.red_social.dto.ContenidoRequest;
import com.redsocial.red_social.dto.ContenidoResponse;
import com.redsocial.red_social.model.*;
import com.redsocial.red_social.repository.ContenidoRepository;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.service.ContenidoService;
import com.redsocial.red_social.service.EstudianteService;
import com.redsocial.red_social.service.ValoracionService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contenido")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;
    private final EstudianteService estudianteService;
    private final ValoracionService valoracionService;
    private final EstudianteRepository estudianteRepository;
    private final ContenidoRepository contenidoRepository;

    @PostMapping("/subir")
    public ResponseEntity<?> subirContenido(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("tipoContenido") TipoContenido tipoContenido,
            @RequestParam("interes") Intereses interes,
            Principal principal) {
        try {
            String username = principal.getName();
            Contenido contenido = contenidoService.guardarContenido(
                    archivo,
                    descripcion,
                    username,
                    tipoContenido,
                    interes
            );

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "status", "success",
                            "message", "Contenido subido con éxito",
                            "contentId", contenido.getId(),
                            "url", "/uploads/" + contenido.getNombreAlmacenado()
                    ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Error al guardar archivo: " + e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/mios")
    public ResponseEntity<List<ContenidoDTO>> obtenerContenidosDelEstudiante(Authentication authentication) {
        String username = authentication.getName();
        List<Contenido> contenidos = contenidoService.obtenerPorAutor(username);

        List<ContenidoDTO> resultado = contenidos.stream()
                .map(contenido -> {
                    ContenidoDTO dto = convertirADTO(contenido);
                    // Asegura que la URL tenga el formato correcto
                    dto.setNombreAlmacenado(contenido.getNombreAlmacenado());
                    dto.setUrl("/uploads/" + contenido.getNombreAlmacenado());

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }



@GetMapping("/explorar")
public ResponseEntity<List<ContenidoDTO>> explorarContenidos() {
    List<Contenido> contenidos = contenidoService.obtenerTodos();

    // Verificación de nulos
    if(contenidos == null || contenidos.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    List<ContenidoDTO> resultado = contenidos.stream()
            .filter(Objects::nonNull) // Filtra nulos
            .map(contenido -> {
                ContenidoDTO dto = convertirADTO(contenido);
                // Asegura URL válida
                if(contenido.getNombreAlmacenado() != null) {
                    dto.setUrl("/uploads/" + contenido.getNombreAlmacenado());
                }
                if(contenido.getTipoContenido() != null) {
                    dto.setTipoContenido(contenido.getTipoContenido());
                }
                if(contenido.getInteres() != null) {
                    dto.setInteres(contenido.getInteres());
                }
                return dto;
            })
            .collect(Collectors.toList());

    return ResponseEntity.ok(resultado);
}
    @PostMapping("/{id}/valorar")
    public ResponseEntity<?> valorarContenido(@PathVariable Long id,
                                              @RequestBody Map<String, Integer> body,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = estudianteService.obtenerPerfilUsuario(userDetails.getUsername());
            if (!(usuario instanceof Estudiante)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los estudiantes pueden valorar contenido.");
            }
            Estudiante estudiante = (Estudiante) usuario;

            Contenido contenido = contenidoService.obtenerPorId(id);

            if (valoracionService.yaValoro(estudiante, contenido)) {
                return ResponseEntity.badRequest().body("Ya has valorado este contenido.");
            }

            int puntuacion = body.getOrDefault("puntuacion", 1); // Valor por defecto: 1
            valoracionService.valorar(estudiante, contenido, puntuacion);

            return ResponseEntity.ok("Contenido valorado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al valorar contenido.");
        }
    }

private ContenidoDTO convertirADTO(Contenido contenido) {
    double promedio = valoracionService.calcularPromedioValoraciones(contenido);
    return ContenidoDTO.builder()
            .id(contenido.getId())
            .nombreOriginal(contenido.getNombreOriginal())
            .tipoArchivo(contenido.getTipoArchivo())
            .tipoContenido(contenido.getTipoContenido())
            .descripcion(contenido.getDescripcion())
            .fechaPublicacion(contenido.getFechaPublicacion())
            .autor(contenido.getAutor().getUsername())
            .likes(contenido.getLikes())
            .promedioValoracion(promedio)
            .url("/uploads/" + contenido.getNombreAlmacenado())
            .build();
}

    private ContenidoDTO convertirADTOConValoracion(Contenido contenido) {
        double promedio = valoracionService.calcularPromedioValoraciones(contenido);

        return ContenidoDTO.builder()
                // ... mismos campos que el método anterior
                .promedioValoracion(promedio)
                .build();
    }

}
