package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.ContenidoRequest;
import com.redsocial.red_social.dto.ContenidoResponse;
import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.service.ContenidoService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/contenido")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    @PostMapping("/subir")
    public ResponseEntity<?> subirContenido(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("descripcion") String descripcion,
            Principal principal) {
        try {
            String username = principal.getName(); // Lo obtenemos del JWT decodificado
            Contenido contenido = contenidoService.guardarContenido(archivo, descripcion, username);
            return ResponseEntity.ok("Contenido subido con éxito. ID: " + contenido.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar archivo");
        }
    }
}
