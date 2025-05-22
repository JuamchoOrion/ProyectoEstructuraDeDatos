package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Data
@Table(name = "solicitudAyuda")
public class SolicitudAyuda implements Comparable<SolicitudAyuda> {

    @ManyToOne(cascade = CascadeType.ALL)
    private Estudiante estudiante;

    @Transient
    private RedSocial redSocial;

    @Column(name = "fechaNecesidad")
    private Date fechaNecesidad;

    @Column(name = "peticion")
    private String peticion;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitudAyuda;

    @Enumerated(EnumType.STRING)
    @Column(name = "interes", nullable = false)
    private Intereses interes;


    public SolicitudAyuda(Date fechaNecesidad, String peticion) {
        this.fechaNecesidad = fechaNecesidad;
        this.peticion = peticion;
    }

    public SolicitudAyuda() {
    }

    @Override
    public int compareTo(SolicitudAyuda otra) {
        // Comparar por proximidad a la fecha actual
        long diffThis = Math.abs(this.fechaNecesidad.getTime() - new Date().getTime());
        long diffOtra = Math.abs(otra.fechaNecesidad.getTime() - new Date().getTime());
        return Long.compare(diffThis, diffOtra);
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
}
