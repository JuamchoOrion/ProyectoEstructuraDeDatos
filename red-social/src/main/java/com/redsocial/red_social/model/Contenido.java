package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante autor;

    @Column(nullable = false)
    private String nombreOriginal;  // Nombre original del archivo

    @Column(nullable = false, unique = true)
    private String nombreAlmacenado; // Nombre único para almacenamiento

    @Column(nullable = false)
    private String tipoArchivo; // "image/jpeg", "application/pdf", etc.

    @Column(nullable = false)
    private Long tamanio; // Tamaño en bytes

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    @Column(nullable = false)
    private Long likes = 0L;

    @Column(nullable = false)
    private String descripcion;

    public void agregarLike() {
        this.likes++;
    }
}