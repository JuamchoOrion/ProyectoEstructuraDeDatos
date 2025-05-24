package com.redsocial.red_social.service;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.GrupoEstudio;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.Valoracion;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.GrupoEstudioRepository;
import com.redsocial.red_social.repository.ValoracionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GrafoService {

    private final GrafoEstudiantes grafoEstudiantes;
    private final EstudianteRepository estudianteRepository;
    private final ValoracionRepository valoracionRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;

    public GrafoService(GrafoEstudiantes grafoEstudiantes,
                        EstudianteRepository estudianteRepository,
                        ValoracionRepository valoracionRepository,
                        GrupoEstudioRepository grupoEstudioRepository) {
        this.grafoEstudiantes = grafoEstudiantes;
        this.estudianteRepository = estudianteRepository;
        this.valoracionRepository = valoracionRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
    }
    @PostConstruct
    @Transactional
    public void inicializarGrafo() {
        List<Estudiante> estudiantes = estudianteRepository.findAllWithIntereses();
        estudiantes.forEach(grafoEstudiantes::agregarEstudiante);

        for (int i = 0; i < estudiantes.size(); i++) {
            for (int j = i + 1; j < estudiantes.size(); j++) {
                Estudiante e1 = estudiantes.get(i);
                Estudiante e2 = estudiantes.get(j);

                int afinidad = calcularAfinidad(e1, e2);
                if (afinidad > 0) {
                    grafoEstudiantes.conectarEstudiantes(e1.getId(), e2.getId(), afinidad);
                }
            }
        }
    }
    /**
     * Calcula la afinidad entre dos estudiantes en base exclusivamente
     * a intereses académicos en común.
     */
    @Transactional(readOnly = true)
    protected int calcularAfinidad(Estudiante e1, Estudiante e2) {
        // Asegúrate de que las colecciones están inicializadas
        Hibernate.initialize(e1.getIntereses());
        Hibernate.initialize(e2.getIntereses());

        int afinidad = 0;

        // Intereses comunes
        Set<Intereses> interesesComunes = new HashSet<>(e1.getIntereses());
        interesesComunes.retainAll(e2.getIntereses());
        afinidad += interesesComunes.size(); // 1 punto por cada interés común

        return afinidad;
    }

    public void actualizarGrafo(Estudiante estudiante) {
        grafoEstudiantes.agregarEstudiante(estudiante);

        // Recalcular conexiones para este estudiante
        List<Estudiante> otrosEstudiantes = estudianteRepository.findAll()
                .stream()
                .filter(e -> !e.getId().equals(estudiante.getId()))
                .collect(Collectors.toList());

        for (Estudiante otro : otrosEstudiantes) {
            int afinidad = calcularAfinidad(estudiante, otro);
            if (afinidad > 0) {
                grafoEstudiantes.conectarEstudiantes(estudiante.getId(), otro.getId(), afinidad);
            }
        }
    }
}