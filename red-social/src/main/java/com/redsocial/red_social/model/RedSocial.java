package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.PriorityQueue;

@Service
@Data
public class RedSocial {


    private Long id;


    private List<Usuario> listaUsuarios;

    private PriorityQueue<SolicitudAyuda> solicitudAyudas;


    private List<Moderador> moderadores;

    private List<GrupoEstudio> listaGrupoEstudios;

    private List<Contenido> listaContenidos;

    public RedSocial(List<Usuario> listaUsuarios, PriorityQueue<SolicitudAyuda> solicitudAyudas, List<Moderador> moderadores, List<GrupoEstudio> listaGrupoEstudios) {
        this.listaUsuarios = listaUsuarios;
        this.solicitudAyudas = solicitudAyudas;
        this.moderadores = moderadores;
        this.listaGrupoEstudios = listaGrupoEstudios;
    }
    public void agregarPublicacion(Contenido contenido){
        listaContenidos.add(contenido);
    }
    public void agregarSolicitudAyuda(SolicitudAyuda solicitudAyuda){
        solicitudAyudas.add(solicitudAyuda);
    }
    public RedSocial() {
    }
    public List<Estudiante> getEstudiantes() {
        List<Estudiante> estudiantes = new ArrayList<>();
        for (Usuario usuario : listaUsuarios) {
            if (usuario instanceof Estudiante) {
                estudiantes.add((Estudiante) usuario);
            }

        }
        return estudiantes;
    }
    public void formarGruposEstudio() {
        // Mapa para agrupar estudiantes por interés
        Map<Intereses, List<Estudiante>> mapaIntereses = new HashMap<>();

        for (Usuario usuario : listaUsuarios) {
            if (usuario instanceof Estudiante estudiante) {
                if (estudiante.getIntereses() != null) {
                    for (Intereses interes : estudiante.getIntereses()) {
                        mapaIntereses
                                .computeIfAbsent(interes, k -> new ArrayList<>())
                                .add(estudiante);
                    }
                }
            }
        }

        // Crear un grupo por cada interés
        for (Map.Entry<Intereses, List<Estudiante>> entrada : mapaIntereses.entrySet()) {
            Intereses interes = entrada.getKey();
            List<Estudiante> estudiantes = entrada.getValue();

            if (estudiantes.size() >= 1) {
                GrupoEstudio grupo = new GrupoEstudio();
                grupo.setListaEstudiantes(estudiantes);
                grupo.setRed_social(this);
                listaGrupoEstudios.add(grupo);

                // Agregar el grupo a cada estudiante
                for (Estudiante estudiante : estudiantes) {
                    estudiante.getGruposEstudioEnlazada().agregar(grupo);
                }
            }
        }
    }



}
