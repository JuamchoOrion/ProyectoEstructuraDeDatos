package com.redsocial.red_social.controller;
import com.redsocial.red_social.dto.EstudianteDTO;
import com.redsocial.red_social.dto.GrafoDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.RedSocial;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.service.EstudianteService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.redsocial.red_social.model.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grafo")
public class GrafoController {

    private final GrafoEstudiantes grafoEstudiantes;
    private final EstudianteService estudianteService;
    private final JwtUtil jwtUtil;
    private final EstudianteRepository estudianteRepository;

    public GrafoController(GrafoEstudiantes grafoEstudiantes,
                           EstudianteService estudianteService,
                           JwtUtil jwtUtil, EstudianteRepository estudianteRepository) {
        this.grafoEstudiantes = grafoEstudiantes;
        this.estudianteService = estudianteService;
        this.jwtUtil = jwtUtil;
        this.estudianteRepository = estudianteRepository;
    }

    // Endpoint para obtener recomendaciones basadas en el grafo
    @GetMapping("/recomendaciones")
    public ResponseEntity<List<EstudianteDTO>> obtenerRecomendaciones(
            @RequestHeader("Authorization") String token) {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Estudiante estudiante = estudianteService.buscarPorUsername(username);

        List<Estudiante> recomendaciones = grafoEstudiantes.obtenerRecomendaciones(estudiante.getId());

        List<EstudianteDTO> resultado = recomendaciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // Endpoint para encontrar el camino más corto entre dos estudiantes
    @GetMapping("/camino/{idDestino}")
    public ResponseEntity<List<EstudianteDTO>> encontrarCamino(
            @RequestHeader("Authorization") String token,
            @PathVariable Long idDestino) {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Estudiante origen = estudianteService.buscarPorUsername(username);


        Estudiante destino = estudianteService.buscarPorId(idDestino);

        List<Estudiante> camino = grafoEstudiantes.encontrarCaminoMasCorto(origen.getId(), destino.getId());

        if (camino.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<EstudianteDTO> resultado = camino.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // Endpoint para detectar comunidades (clústeres)
    @GetMapping("/comunidades")
    public ResponseEntity<List<List<EstudianteDTO>>> detectarComunidades() {
        List<List<Estudiante>> comunidades = grafoEstudiantes.detectarComunidades();

        List<List<EstudianteDTO>> resultado = comunidades.stream()
                .map(comunidad -> comunidad.stream()
                        .map(this::convertirADTO)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // Endpoint para visualizar el grafo completo
    @GetMapping("/visualizar")
    public ResponseEntity<GrafoDTO> visualizarGrafo() {
        GrafoDTO grafoDTO = grafoEstudiantes.visualizar();
        return ResponseEntity.ok(grafoDTO);
    }

    private EstudianteDTO convertirADTO(Estudiante estudiante) {
        return EstudianteDTO.builder()
                .id(estudiante.getId())
                .username(estudiante.getUsername())
                .email(estudiante.getEmail())
                .intereses(estudiante.getIntereses())
                .build();
    }
}
