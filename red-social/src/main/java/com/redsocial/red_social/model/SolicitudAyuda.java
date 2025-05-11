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

    public SolicitudAyuda(Date fechaNecesidad, String peticion) {
        this.fechaNecesidad = fechaNecesidad;
        this.peticion = peticion;
    }

    public SolicitudAyuda() {
    }

    @Override
    public int compareTo(SolicitudAyuda otra) {
        return Long.compare(this.fechaNecesidad.getTime(), otra.fechaNecesidad.getTime());

    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
}
