package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupo_estudio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoEstudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Eliminar @Transient si RedSocial es una entidad, de lo contrario está bien
    @Transient
    private RedSocial red_social;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Intereses interes;
    @ManyToMany
    @JoinTable(
            name = "grupo_estudiante",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> listaEstudiantes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "grupo_contenidos",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "contenido_id")
    )
    private List<Contenido> listaContenidos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "grupo_solicitudes",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "solicitud_id")
    )
    private List<SolicitudAyuda> solicitudAyudas = new ArrayList<>();

    // Métodos auxiliares para manejar relaciones bidireccionales
    public void agregarEstudiante(Estudiante estudiante) {
        listaEstudiantes.add(estudiante);
        estudiante.getGruposEstudio().add(this);
    }

    public void removerEstudiante(Estudiante estudiante) {
        listaEstudiantes.remove(estudiante);
        estudiante.getGruposEstudio().remove(this);
    }
}