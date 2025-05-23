package com.redsocial.red_social.service;

import com.redsocial.red_social.dto.ContenidoDTO;
import com.redsocial.red_social.dto.EstudianteDTO;
import com.redsocial.red_social.dto.GrupoEstudioDTO;
import com.redsocial.red_social.model.*;
import com.redsocial.red_social.repository.ContenidoRepository;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.GrupoEstudioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrupoEstudioService {

    private final GrupoEstudioRepository grupoEstudioRepository;
    private final EstudianteRepository estudianteRepository;
    private final ContenidoRepository contenidoRepository;

    @Transactional
    public void generarGruposDeEstudio() {
        // Obtener todos los intereses posibles
        Intereses[] todosIntereses = Intereses.values();

        // Para cada interés, crear o actualizar grupo
        for (Intereses interes : todosIntereses) {
            GrupoEstudio grupo = grupoEstudioRepository.findByInteres(interes)
                    .orElseGet(() -> crearNuevoGrupo(interes));

            // Obtener estudiantes con este interés
            List<Estudiante> estudiantesInteresados = estudianteRepository
                    .findByInteresesContaining(interes);

            // Agregar estudiantes al grupo si no están ya
            estudiantesInteresados.forEach(estudiante -> {
                if (!grupo.getListaEstudiantes().contains(estudiante)) {
                    grupo.agregarEstudiante(estudiante);
                }
            });

            // Agregar contenidos relacionados
            agregarContenidosAlGrupo(grupo, estudiantesInteresados);

            grupoEstudioRepository.save(grupo);
        }
    }

    private GrupoEstudio crearNuevoGrupo(Intereses interes) {
        GrupoEstudio nuevoGrupo = new GrupoEstudio();
        nuevoGrupo.setInteres(interes);
        nuevoGrupo.setListaEstudiantes(new ArrayList<>());
        nuevoGrupo.setListaContenidos(new ArrayList<>());
        nuevoGrupo.setSolicitudAyudas(new ArrayList<>());
        return grupoEstudioRepository.save(nuevoGrupo);
    }

    private void agregarContenidosAlGrupo(GrupoEstudio grupo, List<Estudiante> estudiantes) {
        estudiantes.forEach(estudiante -> {
            List<Contenido> contenidosEstudiante = contenidoRepository.findByAutor(estudiante);
            contenidosEstudiante.stream()
                    .filter(contenido -> contenido.getInteres() == grupo.getInteres())
                    .forEach(contenido -> {
                        if (!grupo.getListaContenidos().contains(contenido)) {
                            grupo.getListaContenidos().add(contenido);
                        }
                    });
        });
    }

    public List<GrupoEstudioDTO> obtenerGruposPorEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

        return grupoEstudioRepository.findByListaEstudiantesContaining(estudiante)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GrupoEstudioDTO convertToDTO(GrupoEstudio grupo) {
        GrupoEstudioDTO dto = new GrupoEstudioDTO();
        dto.setId(grupo.getId());
        dto.setInteres(grupo.getInteres());
        dto.setContenidos(grupo.getListaContenidos().stream()
                .map(this::convertToContenidoDTO)
                .collect(Collectors.toList()));
        dto.setEstudiantes(grupo.getListaEstudiantes().stream()
                .map(this::convertToEstudianteDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private ContenidoDTO convertToContenidoDTO(Contenido contenido) {
        ContenidoDTO dto = new ContenidoDTO();
        dto.setId(contenido.getId());
        dto.setNombreOriginal(contenido.getNombreOriginal());
        dto.setAutor(contenido.getAutor().getUsername());
        dto.setFechaPublicacion(contenido.getFechaPublicacion());
        dto.setPromedioValoracion(calcularValoracion(contenido.getValoraciones()));
        dto.setTipoArchivo(contenido.getTipoArchivo());
        dto.setDescripcion(contenido.getDescripcion());
        dto.setNombreAlmacenado(contenido.getNombreAlmacenado());
        dto.setTipoContenido(contenido.getTipoContenido());
        return dto;
    }
    private Double calcularValoracion(List<Valoracion> lista) {
        if (lista == null || lista.isEmpty()) {
            return 0.0; // o podrías lanzar una excepción o retornar null según tus necesidades
        }

        double sum = 0.0;
        for (Valoracion v : lista) {
            if (v != null && v.getPuntuacion() != 0) {
                sum += v.getPuntuacion();
            }
        }
        return sum / lista.size();
    }
    private EstudianteDTO convertToEstudianteDTO(Estudiante estudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setId(estudiante.getId());
        dto.setUsername(estudiante.getUsername());
        dto.setEmail(estudiante.getEmail());
        dto.setIntereses(estudiante.getIntereses());
        return dto;
    }
}