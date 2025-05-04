package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Data
@Table(name = "solicitudAyuda")
public class SolicitudAyuda implements Comparable<SolicitudAyuda> {
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
        // A menor diferencia de tiempo con respecto a la fecha actual, mayor prioridad
        long ahora = new Date().getTime();
        long diferenciaEsta = Math.abs(fechaNecesidad.getTime() - ahora);
        long diferenciaOtra = Math.abs(otra.fechaNecesidad.getTime() - ahora);
        return Long.compare(diferenciaEsta, diferenciaOtra);
    }}
