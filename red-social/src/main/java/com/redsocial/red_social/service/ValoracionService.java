package com.redsocial.red_social.service;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Valoracion;
import com.redsocial.red_social.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;

    /**
     * Verifica si el estudiante ya valoró un contenido.
     */
    public boolean yaValoro(Estudiante estudiante, Contenido contenido) {
        return valoracionRepository.existsByEstudianteAndContenido(estudiante, contenido);
    }

    /**
     * Crea una nueva valoración.
     * @param estudiante el estudiante que valora
     * @param contenido el contenido que está siendo valorado
     * @param puntuacion puede ser 1 (like), -1 (dislike), o un número del 1 al 5
     */
    public Valoracion valorar(Estudiante estudiante, Contenido contenido, int puntuacion) {
        Valoracion valoracion = new Valoracion();
        valoracion.setEstudiante(estudiante);
        valoracion.setContenido(contenido);
        valoracion.setPuntuacion(puntuacion);
        valoracion.setFecha(LocalDateTime.now());
        // si deseas permitir comentarios reales, ajusta esto

        // Guardar la valoración
        Valoracion guardada = valoracionRepository.save(valoracion);

        // Actualizar contador de likes del contenido
        if (puntuacion == 1) {
            contenido.setLikes(contenido.getLikes() + 1);
        }
        // Podrías manejar otros tipos de puntuación o dislikes aquí

        return guardada;
    }

    /**
     * Recupera todas las valoraciones de un contenido.
     */
    public List<Valoracion> obtenerValoracionesDeContenido(Contenido contenido) {
        return valoracionRepository.findByContenido(contenido);
    }
}
