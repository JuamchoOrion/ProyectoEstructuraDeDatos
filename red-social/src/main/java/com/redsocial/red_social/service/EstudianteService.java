package com.redsocial.red_social.service;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Usuario;
import com.redsocial.red_social.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EstudianteService  {
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired  // ¡Asegúrate que esté inyectado!
    private PasswordEncoder passwordEncoder;

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

    public Estudiante buscarPorUsername(String username) {
        return estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Estudiante no encontrado"));
    }

    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
    }

    @Transactional
    public void agregarAmigo(Long idEstudiante1, Long idEstudiante2) {
        Estudiante estudiante1 = estudianteRepository.findById(idEstudiante1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 1 no encontrado"));

        Estudiante estudiante2 = estudianteRepository.findById(idEstudiante2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 2 no encontrado"));

        // Validar que no sean el mismo estudiante
        if (estudiante1.getId().equals(estudiante2.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un estudiante no puede ser amigo de sí mismo");
        }

        // Agregar amistad bidireccional
        estudiante1.agregarAmigo(estudiante2);
        estudiante2.agregarAmigo(estudiante1);

        // Guardar cambios
        estudianteRepository.save(estudiante1);
        estudianteRepository.save(estudiante2);
    }

    // Método para eliminar amistad
    @Transactional
    public void eliminarAmigo(Long idEstudiante1, Long idEstudiante2) {
        Estudiante estudiante1 = estudianteRepository.findById(idEstudiante1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 1 no encontrado"));

        Estudiante estudiante2 = estudianteRepository.findById(idEstudiante2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 2 no encontrado"));

        // Eliminar amistad bidireccional
        estudiante1.eliminarAmigo(estudiante2);
        estudiante2.eliminarAmigo(estudiante1);

        // Guardar cambios
        estudianteRepository.save(estudiante1);
        estudianteRepository.save(estudiante2);
    }

    // Método para obtener amigos de un estudiante
    public List<Estudiante> obtenerAmigos(Long idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        return new ArrayList<>(estudiante.getAmigos());
    }
    public List<Estudiante> obtenerEstudiantesConParticipacion() {
        return estudianteRepository.findByContenidosPublicadosIsNotEmpty();
    }

    // Método para verificar si dos estudiantes son amigos
    public boolean sonAmigos(Long idEstudiante1, Long idEstudiante2) {
        Estudiante estudiante1 = estudianteRepository.findById(idEstudiante1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 1 no encontrado"));

        Estudiante estudiante2 = estudianteRepository.findById(idEstudiante2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante 2 no encontrado"));

        return estudiante1.getAmigos().contains(estudiante2);
    }
}