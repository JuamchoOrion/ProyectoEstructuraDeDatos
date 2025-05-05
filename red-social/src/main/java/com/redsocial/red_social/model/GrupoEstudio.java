package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Transient
    private RedSocial red_social;

    @ManyToMany
    @JoinTable(
            name = "grupo_estudiante",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> listaEstudiantes;

    @Transient
    private List<Contenido> listaContenidos;
}
