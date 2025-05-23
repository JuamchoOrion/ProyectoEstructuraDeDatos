package com.redsocial.red_social.dto;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Intereses;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.redsocial.red_social.model.Intereses;
import java.util.List;

public class GrupoEstudioDTO {
    private Long id;
    private Intereses interes;
    private List<EstudianteDTO> estudiantes;
    private List<ContenidoDTO> contenidos;

    // Constructor público
    public GrupoEstudioDTO(Long id, Intereses interes, List<EstudianteDTO> estudiantes, List<ContenidoDTO> contenidos) {
        this.id = id;
        this.interes = interes;
        this.estudiantes = estudiantes;
        this.contenidos = contenidos;
    }

    public List<ContenidoDTO> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<ContenidoDTO> contenidos) {
        this.contenidos = contenidos;
    }

    // Getters y Setters públicos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Intereses getInteres() {
        return interes;
    }

    public void setInteres(Intereses interes) {
        this.interes = interes;
    }

    public List<EstudianteDTO> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteDTO> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public GrupoEstudioDTO() {
    }
}