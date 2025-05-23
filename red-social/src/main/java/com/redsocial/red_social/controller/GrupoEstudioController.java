package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.GrupoEstudioDTO;
import com.redsocial.red_social.model.GrupoEstudio;
import com.redsocial.red_social.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}