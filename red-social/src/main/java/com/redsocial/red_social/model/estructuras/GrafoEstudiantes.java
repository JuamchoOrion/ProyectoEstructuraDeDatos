package com.redsocial.red_social.model.estructuras;

import com.redsocial.red_social.model.Estudiante;

import java.util.*;

public class GrafoEstudiantes {

    private Map<Estudiante, Set<Estudiante>> grafo = new HashMap<>();

    public GrafoEstudiantes(List<Estudiante> estudiantes) {
        construirGrafo(estudiantes);
    }

    private void construirGrafo(List<Estudiante> estudiantes) {
        for (Estudiante e1 : estudiantes) {
            for (Estudiante e2 : estudiantes) {
                if (!e1.equals(e2) && tienenGustoEnComun(e1, e2)) {
                    grafo.computeIfAbsent(e1, k -> new HashSet<>()).add(e2);
                    grafo.computeIfAbsent(e2, k -> new HashSet<>()).add(e1); // no dirigido
                }
            }
        }
    }

    private boolean tienenGustoEnComun(Estudiante e1, Estudiante e2) {
        return e1.getIntereses().stream().anyMatch(e2.getIntereses()::contains);
    }

    public Map<Estudiante, Set<Estudiante>> getGrafo() {
        return grafo;
    }
}

