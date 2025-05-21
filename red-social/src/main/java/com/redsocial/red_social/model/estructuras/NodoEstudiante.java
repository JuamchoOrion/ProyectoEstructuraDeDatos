package com.redsocial.red_social.model.estructuras;

import com.redsocial.red_social.model.Estudiante;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NodoEstudiante {
    private Estudiante estudiante;
    private List<NodoEstudiante> vecinos = new ArrayList<>();

    public NodoEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    // Getters y setters
    public void agregarVecino(NodoEstudiante vecino) {
        if (!vecinos.contains(vecino)) {
            vecinos.add(vecino);
        }
    }
}