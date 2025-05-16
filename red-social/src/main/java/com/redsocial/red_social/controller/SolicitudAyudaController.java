package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.SolicitudRequestDTO;
import com.redsocial.red_social.dto.SolicitudResponseDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.SolicitudAyuda;
import com.redsocial.red_social.service.EstudianteService;
import com.redsocial.red_social.service.SolicitudAyudaService;
import com.redsocial.red_social.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudAyudaController {

    private final SolicitudAyudaService solicitudService;
    private final JwtUtil jwtUtil;
    private final EstudianteService estudianteService;
/**
    @GetMapping
    public ResponseEntity<List<SolicitudAyuda>> obtenerSolicitudes() {
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesOrdenadas());
    }
**/
@PostMapping
public ResponseEntity<SolicitudResponseDTO> crearSolicitud(
        @RequestBody SolicitudRequestDTO requestDTO,
        Authentication authentication) {

    String username = authentication.getName(); // obtenido del token JWT
    return ResponseEntity.ok(solicitudService.crearSolicitud(username, requestDTO));
}

    @GetMapping("/urgentes")
    public ResponseEntity<List<SolicitudResponseDTO>> obtenerSolicitudesUrgentes(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));

        List<SolicitudAyuda> solicitudes = solicitudService.obtenerSolicitudesUrgentes(username);

        List<SolicitudResponseDTO> dtos = solicitudes.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private SolicitudResponseDTO convertirAResponseDTO(SolicitudAyuda solicitud) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(solicitud.getIdSolicitudAyuda());
        dto.setFechaNecesidad(solicitud.getFechaNecesidad());
        dto.setPeticion(solicitud.getPeticion());
        dto.setInteres(solicitud.getInteres());
        dto.setNombreEstudiante(solicitud.getEstudiante().getUsername());
        return dto;
    }

}