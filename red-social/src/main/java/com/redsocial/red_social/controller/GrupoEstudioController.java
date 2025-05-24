package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.*;
import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.GrupoEstudio;
import com.redsocial.red_social.model.TipoContenido;
import com.redsocial.red_social.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos-estudio")
@RequiredArgsConstructor
public class GrupoEstudioController {

    private final GrupoEstudioService grupoEstudioService;

    @PostMapping("/generar")
    public ResponseEntity<Void> generarGrupos() {
        grupoEstudioService.generarGruposDeEstudio();
        return ResponseEntity.ok().build();
    }
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<GrupoEstudioDTO>> obtenerGruposDeEstudiante(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(grupoEstudioService.obtenerGruposPorEstudiante(estudianteId));
    }

    @GetMapping("/{grupoId}")
    public ResponseEntity<GrupoDetalleDTO> obtenerDetallesGrupo(
            @PathVariable Long grupoId,
            Authentication authentication) {

        // El username se obtiene automáticamente del token JWT
        String username = authentication.getName();

        return ResponseEntity.ok(grupoEstudioService.obtenerDetallesGrupo(grupoId, username));
    }

    @PostMapping("/{grupoId}/contenidos")
    public ResponseEntity<?> publicarContenidoEnGrupo(
            @PathVariable Long grupoId,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("tipoContenido") TipoContenido tipoContenido,
            Principal principal) {

        try {
            String username = principal.getName();
            ContenidoDTO contenidoDTO = grupoEstudioService.agregarContenidoAGrupo(
                    grupoId,
                    archivo,
                    descripcion,
                    username,
                    tipoContenido
            );

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Contenido publicado correctamente",
                            "contenido", contenidoDTO
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
    @PostMapping("/{grupoId}/solicitudes")
    public ResponseEntity<SolicitudResponseDTO> crearSolicitudEnGrupo(
            @PathVariable Long grupoId,
            @RequestBody SolicitudRequestDTO requestDTO,
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(
                grupoEstudioService.crearSolicitudEnGrupo(grupoId, username, requestDTO)
        );
    }
}