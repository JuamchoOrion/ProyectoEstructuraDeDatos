package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "contenido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // Cambiado a LAZY para mejor performance
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante autor;

    @OneToMany(mappedBy = "contenido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoracion> valoraciones = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Intereses interes;

    @Column(nullable = false)
    private String nombreOriginal;

    @Column(nullable = false, unique = true)
    private String nombreAlmacenado;

    @Column(nullable = false)
    private String tipoArchivo;

    @Column(nullable = false)
    private Long tamanio;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipoContenido;

    @Column(nullable = false)
    private Long likes = 0L;

    @Column(nullable = false, length = 1000) // Aumentar longitud para descripción
    private String descripcion;

    @ManyToMany(mappedBy = "listaContenidos")
    private List<GrupoEstudio> grupos = new ArrayList<>();
    // Métodos para manejar relaciones
    public void agregarValoracion(Valoracion valoracion) {
        valoraciones.add(valoracion);
        valoracion.setContenido(this);
    }

    public void removerValoracion(Valoracion valoracion) {
        valoraciones.remove(valoracion);
        valoracion.setContenido(null);
    }

    public void agregarLike() {
        this.likes++;
    }
}