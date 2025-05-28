package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.MensajeChatDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.MensajeChat;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.MensajeChatRepository;
import com.redsocial.red_social.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeChatRepository mensajeRepo;
    private final JwtUtil jwtUtil;
    private final EstudianteRepository estudianteRepository;
    public ChatController(SimpMessagingTemplate messagingTemplate, MensajeChatRepository mensajeRepo, JwtUtil jwtUtil, EstudianteRepository estudianteRepository) {
        this.messagingTemplate = messagingTemplate;
        this.mensajeRepo = mensajeRepo;
        this.jwtUtil = jwtUtil;
        this.estudianteRepository = estudianteRepository;
    }
    @MessageMapping("/enviar")
    public void enviarMensaje(@Payload MensajeChatDTO mensaje, Principal principal) {
        // Validaciones adicionales
        if (principal == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        if (mensaje.getReceptorId() == null) {
            throw new IllegalArgumentException("Receptor no especificado");
        }

        Estudiante emisor = estudianteRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asegurar que el mensaje tenga el ID del emisor
        mensaje.setEmisorId(emisor.getId());

        // Guardar en base de datos
        MensajeChat nuevo = new MensajeChat();
        nuevo.setEmisorId(emisor.getId());
        nuevo.setReceptorId(mensaje.getReceptorId());
        nuevo.setContenido(mensaje.getContenido());
        nuevo.setTimestamp(LocalDateTime.now());
        mensajeRepo.save(nuevo);

        // Enviar mensaje con información completa
        messagingTemplate.convertAndSend("/tema/chat/" + mensaje.getReceptorId(), mensaje);
    }
    /**
    @MessageMapping("/enviar")
    public void enviarMensaje(MensajeChatDTO mensaje, Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token no proporcionado");
        }

        String token = authHeader.substring(7); // Quitar "Bearer "
        String username = jwtUtil.extractUsername(token); // Usa tu clase JwtUtil

        Estudiante emisor = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MensajeChat nuevo = new MensajeChat();
        nuevo.setEmisorId(emisor.getId());
        nuevo.setReceptorId(mensaje.getReceptorId());
        nuevo.setContenido(mensaje.getContenido());
        nuevo.setTimestamp(LocalDateTime.now());

        mensajeRepo.save(nuevo);

        messagingTemplate.convertAndSend(
                "/tema/chat/" + mensaje.getReceptorId(), mensaje
        );
    }**/@GetMapping("/chat/{amigoId}")
    public ResponseEntity<?> getMensajesChat(@PathVariable Long amigoId, Authentication authentication) {
        String username = authentication.getName();
        Estudiante yo = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<MensajeChat> mensajes = mensajeRepo.findByEmisorIdAndReceptorIdOrReceptorIdAndEmisorId(
                yo.getId(), amigoId, yo.getId(), amigoId
        );

        Map<Long, String> nombres = new HashMap<>();
        nombres.put(yo.getId(), yo.getUsername());

        // Pre-carga el amigo
        Estudiante amigo = estudianteRepository.findById(amigoId)
                .orElseThrow(() -> new RuntimeException("Amigo no encontrado"));
        nombres.put(amigo.getId(), amigo.getUsername());

        List<Map<String, Object>> response = mensajes.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("emisorId", m.getEmisorId());
            map.put("receptorId", m.getReceptorId());
            map.put("contenido", m.getContenido());
            map.put("timestamp", m.getTimestamp());
            map.put("emisorNombre", nombres.get(m.getEmisorId()));
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}