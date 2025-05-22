package com.redsocial.red_social.model.estructuras;

import lombok.Data;

@Data
// AristaAfinidad.java
public class AristaAfinidad {
    private NodoEstudiante estudiante1;
    private NodoEstudiante estudiante2;
    private int pesoAfinidad; // Basado en intereses comunes, grupos compartidos, etc.

    public AristaAfinidad(NodoEstudiante estudiante1, NodoEstudiante estudiante2, int pesoAfinidad) {
        this.estudiante1 = estudiante1;
        this.estudiante2 = estudiante2;
        this.pesoAfinidad = pesoAfinidad;
    }

    public AristaAfinidad() {
    }
// Constructor, getters y setters
}