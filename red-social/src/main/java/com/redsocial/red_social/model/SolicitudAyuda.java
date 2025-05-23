package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "solicitud_ayuda") // Mejor nombre de tabla
public class SolicitudAyuda implements Comparable<SolicitudAyuda> {
    @ManyToMany(mappedBy = "solicitudAyudas")
    private List<GrupoEstudio> grupos = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitudAyuda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Transient
    private RedSocial redSocial;

    @Column(name = "fecha_necesidad") // Mejor nombre de columna
    private Date fechaNecesidad;

    @Column(name = "peticion", length = 1000) // Aumentar longitud
    private String peticion;

    @Enumerated(EnumType.STRING)
    @Column(name = "interes", nullable = false)
    private Intereses interes;

    // Constructor con campos requeridos
    public SolicitudAyuda(Estudiante estudiante, Date fechaNecesidad, String peticion, Intereses interes) {
        this.estudiante = estudiante;
        this.fechaNecesidad = fechaNecesidad;
        this.peticion = peticion;
        this.interes = interes;
    }

    public SolicitudAyuda() {
    }

    @Override
    public int compareTo(SolicitudAyuda otra) {
        if (this.fechaNecesidad == null || otra.fechaNecesidad == null) {
            return 0;
        }
        long diffThis = Math.abs(this.fechaNecesidad.getTime() - System.currentTimeMillis());
        long diffOtra = Math.abs(otra.fechaNecesidad.getTime() - System.currentTimeMillis());
        return Long.compare(diffThis, diffOtra);
    }
}