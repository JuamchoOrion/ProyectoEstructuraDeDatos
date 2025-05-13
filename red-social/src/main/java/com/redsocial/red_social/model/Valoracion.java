package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "valoracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "contenido_id")
    private Contenido contenido;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private int puntuacion; // Por ahora puede ser 1 (like)
}
