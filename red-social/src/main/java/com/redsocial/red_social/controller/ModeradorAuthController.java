package com.redsocial.red_social.controller;

import com.redsocial.red_social.dto.AuthRequest;
import com.redsocial.red_social.dto.AuthResponse;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.repository.ModeradorRepository;
import com.redsocial.red_social.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/moderadores/auth")
public class ModeradorAuthController {

    private final AuthenticationManager authenticationManager;
    private final ModeradorRepository moderadorRepository;
    private final JwtUtil jwtUtil;
    @Autowired
    public ModeradorAuthController(
            AuthenticationManager authenticationManager,
            ModeradorRepository moderadorRepository,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.moderadorRepository = moderadorRepository;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginModerador(@RequestBody AuthRequest authRequest) {
        try {
            // Autenticación general primero
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Verificación específica de moderador (sin segunda consulta)
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            if (!userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("MODERADOR"))) {
                throw new BadCredentialsException("No es un moderador");
            }

            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}