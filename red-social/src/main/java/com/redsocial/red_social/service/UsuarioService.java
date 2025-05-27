package com.redsocial.red_social.service;

import com.redsocial.red_social.dto.UsuarioDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;

    public List<UsuarioDTO> obtenerTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Convertir lista de entidades a lista de DTOs
        return usuarios.stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getClass().getSimpleName()// tipoUsuario: Estudiante o Moderador
                ))
                .collect(Collectors.toList());
    }
    public List<UsuarioDTO> obtenerTodosFiltrado(String username) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.removeIf(usuario -> usuario.getUsername().equals(username));
        // Convertir lista de entidades a lista de DTOs
        return usuarios.stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getClass().getSimpleName()// tipoUsuario: Estudiante o Moderador
                ))
                .collect(Collectors.toList());
    }



    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }


}
