package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.ContenidoDTO;
import com.redsocial.red_social.dto.ContenidoRequest;
import com.redsocial.red_social.dto.ContenidoResponse;
import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.TipoContenido;
import com.redsocial.red_social.repository.ContenidoRepository;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.service.ContenidoService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contenido")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;
    private final EstudianteRepository estudianteRepository;
    private final ContenidoRepository contenidoRepository;

    @PostMapping("/subir")
    public ResponseEntity<?> subirContenido(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("tipoContenido") TipoContenido tipoContenido,
            Principal principal) {
        try {
            String username = principal.getName(); // Lo obtenemos del JWT decodificado
            Contenido contenido = contenidoService.guardarContenido(archivo, descripcion, username, tipoContenido);
            return ResponseEntity.ok("Contenido subido con éxito. ID: " + contenido.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar archivo");
        }
    }

    @GetMapping("/mios")
    public ResponseEntity<List<ContenidoDTO>> obtenerContenidosDelEstudiante(Authentication authentication) {
        String username = authentication.getName();
        Estudiante estudiante = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));

        List<Contenido> contenidos = contenidoRepository.findByAutor(estudiante);

        List<ContenidoDTO> resultado = contenidos.stream()
                .map(contenido -> ContenidoDTO.builder()
                        .id(contenido.getId())
                        .nombreOriginal(contenido.getNombreOriginal())
                        .tipoArchivo(contenido.getTipoArchivo())
                        .tipoContenido(contenido.getTipoContenido())
                        .descripcion(contenido.getDescripcion())
                        .fechaPublicacion(contenido.getFechaPublicacion())
                        .autor(contenido.getAutor().getUsername())
                        .likes(contenido.getLikes())
                        .url("/uploads/" + contenido.getNombreAlmacenado())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/explorar")
    public ResponseEntity<List<ContenidoDTO>> explorarContenidos() {
        List<Contenido> contenidos = contenidoRepository.findAll();  // Obtener todos los contenidos

        List<ContenidoDTO> resultado = contenidos.stream()
                .map(contenido -> ContenidoDTO.builder()
                        .id(contenido.getId())
                        .nombreOriginal(contenido.getNombreOriginal())
                        .tipoArchivo(contenido.getTipoArchivo())
                        .descripcion(contenido.getDescripcion())
                        .fechaPublicacion(contenido.getFechaPublicacion())
                        .likes(contenido.getLikes())
                        .autor(contenido.getAutor().getUsername())
                        .url("/uploads/" + contenido.getNombreAlmacenado())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

}
