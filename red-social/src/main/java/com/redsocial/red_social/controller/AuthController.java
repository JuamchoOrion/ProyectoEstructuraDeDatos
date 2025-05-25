package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.AuthRequest;
import com.redsocial.red_social.dto.AuthResponse;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.service.EstudianteService;
import com.redsocial.red_social.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final EstudianteService estudianteService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          EstudianteService estudianteService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.estudianteService = estudianteService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/index")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Cargar detalles de usuario
            final UserDetails userDetails = estudianteService.loadUserByUsername(authRequest.getUsername());

            // Buscar el estudiante para obtener el ID
            Estudiante estudiante = estudianteService.buscarPorUsername(authRequest.getUsername());

            // Generar token con el ID incluido
            final String jwt = jwtUtil.generateToken(userDetails, estudiante.getId());

            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }



    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = estudianteService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    return ResponseEntity.ok().body("Token válido");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se proporcionó token");
    }
}
