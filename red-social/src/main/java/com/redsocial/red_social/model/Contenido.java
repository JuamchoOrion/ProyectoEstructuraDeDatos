package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contenido")
@Data
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @Transient
    private RedSocial red_social;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    @Column(name = "archivo", nullable = false, length = 512)
    private String archivo;

    @ManyToMany(mappedBy = "contenidosValorados")
    private List<Estudiante> estudiantesQueValoraron;

    @Column(name = "likes")
    private Long likes;

    public Contenido() {
    }

    public Contenido(Long id, LocalDate fechaPublicacion, String archivo) {
        this.id = id;
        this.fechaPublicacion = fechaPublicacion;
        this.archivo = archivo;
        this.likes = 0L;
    }

    public void agregarLike() {
        this.likes++;
    }
}
