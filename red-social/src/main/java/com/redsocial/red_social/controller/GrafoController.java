package com.redsocial.red_social.controller;
import com.redsocial.red_social.dto.CaminoDTO;
import com.redsocial.red_social.dto.EstudianteConConexionDTO;
import com.redsocial.red_social.dto.EstudianteDTO;
import com.redsocial.red_social.dto.GrafoDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.RedSocial;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.service.EstudianteService;
import com.redsocial.red_social.service.ModeradorService;
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
    private final ModeradorService moderadorService;

    public GrafoController(GrafoEstudiantes grafoEstudiantes,
                           EstudianteService estudianteService,
                           JwtUtil jwtUtil, EstudianteRepository estudianteRepository, ModeradorService moderadorService) {
        this.grafoEstudiantes = grafoEstudiantes;
        this.estudianteService = estudianteService;
        this.jwtUtil = jwtUtil;
        this.estudianteRepository = estudianteRepository;
        this.moderadorService = moderadorService;
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
    @GetMapping("/caminoCorto")
    public ResponseEntity<List<CaminoDTO>> obtenerCaminosMasCortos() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();
        List<CaminoDTO> caminos = new ArrayList<>();

        Set<String> paresProcesados = new HashSet<>();

        for (Estudiante e1 : estudiantes) {
            for (Estudiante e2 : estudiantes) {
                if (!e1.equals(e2)) {
                    // Evitar procesar el mismo par en orden inverso
                    String clavePar = e1.getId() + "-" + e2.getId();
                    String claveParInversa = e2.getId() + "-" + e1.getId();
                    if (paresProcesados.contains(clavePar) || paresProcesados.contains(claveParInversa)) {
                        continue;
                    }

                    List<Estudiante> camino = grafoEstudiantes.encontrarCaminoMasCorto(e1.getId(), e2.getId());

                    if (camino != null && camino.size() > 1) {
                        CaminoDTO dto = new CaminoDTO();
                        dto.setEstudianteA(e1.getUsername());
                        dto.setEstudianteB(e2.getUsername());
                        dto.setCamino(camino.stream().map(Estudiante::getUsername).collect(Collectors.toList()));
                        dto.setLongitud(camino.size()-1);
                        caminos.add(dto);

                        paresProcesados.add(clavePar);
                        paresProcesados.add(claveParInversa);
                    }
                }
            }
        }

        return ResponseEntity.ok(caminos);
    }

    @GetMapping("/conexiones")
    public ResponseEntity<List<EstudianteConConexionDTO>> obtenerConexionesEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();
        List<EstudianteConConexionDTO> resultado = new ArrayList<>();

        for (Estudiante estudiante : estudiantes) {
            int conexiones = grafoEstudiantes.obtenerVecinosEstudiante(estudiante.getId()).size();
            EstudianteConConexionDTO dto = new EstudianteConConexionDTO();
            dto.setId(estudiante.getId());
            dto.setUsername(estudiante.getUsername());
            dto.setEmail(estudiante.getEmail());
            dto.setConexiones(conexiones);
            resultado.add(dto);
        }
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
