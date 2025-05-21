package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;


@Entity
@DiscriminatorValue("MODERADOR")
@NoArgsConstructor // Constructor sin argumentos (requerido por JPA)
@Data
public class Moderador extends Usuario {



    @Transient
    private RedSocial red_social;

    public Moderador(String nombre, String contrasenia, String email) {
        super(nombre, contrasenia, email);
    }


    public Map<String, Set<String>> construirGrafoPorGustos() {
        Map<String, Set<String>> grafo = new HashMap<>();
        List<Estudiante> estudiantes = red_social.getEstudiantes();

        for (Estudiante e1 : estudiantes) {
            Set<String> vecinos = new HashSet<>();
            for (Estudiante e2 : estudiantes) {
                if (!e1.equals(e2) && tienenGustosEnComun(e1, e2)) {
                    vecinos.add(e2.getUsername());
                }
            }
            grafo.put(e1.getUsername(), vecinos);
        }
        return grafo;
    }

    private boolean tienenGustosEnComun(Estudiante e1, Estudiante e2) {
        if (e1.getIntereses() == null || e2.getIntereses() == null) return false;
        for (Intereses gusto : e1.getIntereses()) {
            if (e2.getIntereses().contains(gusto)) return true;
        }
        return false;
    }
}
