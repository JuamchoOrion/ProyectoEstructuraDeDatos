package com.redsocial.red_social.service;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EstudianteService implements UserDetailsService {
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired  // ¡Asegúrate que esté inyectado!
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = estudianteRepository.findByUsername(username) // Busca en usuario
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getUsername(), // Usa el username para autenticar
                usuario.getPassword(),
                new ArrayList<>()
        );
    }
    public Usuario obtenerPerfilUsuario(String username) {
        return estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}