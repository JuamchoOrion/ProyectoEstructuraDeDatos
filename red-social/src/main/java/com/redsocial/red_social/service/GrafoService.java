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

    public void inicializarGrafo() {
        this.inicializarGrafoTransactional();
    }

    @Transactional
    public void inicializarGrafoTransactional() {
        List<Estudiante> estudiantes = estudianteRepository.findAllConRelacionesCompletas();
        estudiantes.forEach(grafoEstudiantes::agregarEstudiante);

        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante e1 = estudiantes.get(i);
            for (int j = i + 1; j < estudiantes.size(); j++) {
                Estudiante e2 = estudiantes.get(j);
                int afinidad = calcularAfinidad(e1, e2);
                if (afinidad > 0) {
                    grafoEstudiantes.conectarEstudiantes(e1.getId(), e2.getId(), afinidad);
                }
            }
        }
    }

    private boolean tienenInteresesComunes(Estudiante e1, Estudiante e2) {
        Set<Intereses> interesesComunes = new HashSet<>(e1.getIntereses());
        interesesComunes.retainAll(e2.getIntereses());
        return !interesesComunes.isEmpty();
    }

    private void inicializarColeccionesEstudiante(Estudiante estudiante) {
        Hibernate.initialize(estudiante.getSolicitudesAyuda());
        Hibernate.initialize(estudiante.getContenidosPublicados());
        Hibernate.initialize(estudiante.getGruposEstudio());
        Hibernate.initialize(estudiante.getValoraciones());
    }

    @Transactional(readOnly = true)
    protected int calcularAfinidad(Estudiante e1, Estudiante e2) {
        Hibernate.initialize(e1.getIntereses());
        Hibernate.initialize(e2.getIntereses());

        Set<Intereses> interesesComunes = new HashSet<>(e1.getIntereses());
        interesesComunes.retainAll(e2.getIntereses());

        return interesesComunes.size();
    }

    public void actualizarGrafo(Estudiante estudiante) {
        grafoEstudiantes.agregarEstudiante(estudiante);

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


/**
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

    public void inicializarGrafo() {
        this.inicializarGrafoTransactional(); // sigue delegando
    }


    @Transactional
    public void inicializarGrafoTransactional() {
        List<Estudiante> estudiantes = estudianteRepository.findAllConRelacionesCompletas();
        estudiantes.forEach(grafoEstudiantes::agregarEstudiante);

        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante e1 = estudiantes.get(i);
            for (int j = i + 1; j < estudiantes.size(); j++) {
                Estudiante e2 = estudiantes.get(j);
                int afinidad = calcularAfinidad(e1, e2);
                if (afinidad > 0) {
                    grafoEstudiantes.conectarEstudiantes(e1.getId(), e2.getId(), afinidad);
                    establecerAmistadSiNoExiste(e1, e2);
                }
            }
        }
    }


    private boolean tienenInteresesComunes(Estudiante e1, Estudiante e2) {
        Set<Intereses> interesesComunes = new HashSet<>(e1.getIntereses());
        interesesComunes.retainAll(e2.getIntereses());
        return !interesesComunes.isEmpty();
    }
    private void establecerAmistadSiNoExiste(Estudiante e1, Estudiante e2) {
        // Verifica si ya son amigos sin disparar la sincronización
        boolean yaSonAmigos = e1.getAmigos().stream()
                .anyMatch(amigo -> amigo.getId().equals(e2.getId()));

        if (!yaSonAmigos) {
            // Establece la amistad sin usar los métodos de sincronización
            e1.getAmigos().add(e2);
            e2.getAmigos().add(e1);

            // Guarda los cambios
            estudianteRepository.saveAll(List.of(e1, e2));

            // Sincroniza después de guardar
            sincronizarAmistades(e1);
            sincronizarAmistades(e2);
        }
    }
    private void sincronizarAmistades(Estudiante estudiante) {
        try {
            estudiante.sincronizarAPersistencia();
            estudiante.sincronizarAEnlazadas();
        } catch (Exception e) {

        }
    }
    private void inicializarColeccionesEstudiante(Estudiante estudiante) {
        Hibernate.initialize(estudiante.getSolicitudesAyuda());
        Hibernate.initialize(estudiante.getContenidosPublicados());
        Hibernate.initialize(estudiante.getGruposEstudio());
        Hibernate.initialize(estudiante.getValoraciones());
        // Inicializa cualquier otra colección que usen los métodos de sincronización
    }
    /**
     * Calcula la afinidad entre dos estudiantes en base exclusivamente
     * a intereses académicos en común.
     *
    @Transactional(readOnly = true)
    protected int calcularAfinidad(Estudiante e1, Estudiante e2) {
        // Inicializa solo lo necesario
        Hibernate.initialize(e1.getIntereses());
        Hibernate.initialize(e2.getIntereses());

        int afinidad = 0;
        Set<Intereses> interesesComunes = new HashSet<>(e1.getIntereses());
        interesesComunes.retainAll(e2.getIntereses());

        if (!interesesComunes.isEmpty()) {
            // Usa el nuevo método seguro
            establecerAmistadSiNoExiste(e1, e2);
            afinidad += interesesComunes.size();
        }

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
    private void agregarAmistadSiNoExiste(Estudiante e1, Estudiante e2) {
        // Inicializa manualmente las colecciones necesarias
        Hibernate.initialize(e1.getSolicitudesAyuda());
        Hibernate.initialize(e2.getSolicitudesAyuda());
        Hibernate.initialize(e1.getContenidosPublicados());
        Hibernate.initialize(e2.getContenidosPublicados());

        if (!e1.getAmigos().contains(e2)) {
            e1.agregarAmigo(e2);
            e2.agregarAmigo(e1);
            estudianteRepository.saveAll(List.of(e1, e2));
        }
    }
}**/