package com.redsocial.red_social.service;

import com.redsocial.red_social.dto.RegistroModeradorDTO;
import com.redsocial.red_social.model.Moderador;
import com.redsocial.red_social.repository.ModeradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ModeradorService  {

    @Autowired
    private ModeradorRepository moderadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Autenticación para Spring Security

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Moderador moderador = moderadorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Moderador no encontrado"));

        return new User(
                moderador.getUsername(),
                moderador.getPassword(),
                new ArrayList<>() // Agrega roles si es necesario
        );
    }

    // Registro de moderador con contraseña encriptada
    public Moderador registrarModerador(RegistroModeradorDTO dto) {
        if (moderadorRepository.findByEmail(dto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        Moderador moderador = new Moderador();
        moderador.setUsername(dto.getNombre());
        moderador.setEmail(dto.getCorreo());
        moderador.setPassword(passwordEncoder.encode(dto.getContrasena()));

        return moderadorRepository.save(moderador);
    }

    // Obtener perfil del moderador por username
    public Moderador obtenerPerfilModerador(String username) {
        return moderadorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Moderador no encontrado"));
    }
}