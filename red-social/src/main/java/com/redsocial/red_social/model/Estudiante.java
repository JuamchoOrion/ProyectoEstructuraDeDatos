package com.redsocial.red_social.model;

import com.redsocial.red_social.model.estructuras.ListaEnlazada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ESTUDIANTE")
@NoArgsConstructor
@Getter
@Setter
public class Estudiante extends Usuario implements Comparable<Estudiante> {

    // Relaciones JPA estándar (para persistencia)
    @ManyToMany
    @JoinTable(
            name = "contenido_valorado",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "contenido_id")
    )
    private List<Contenido> contenidosValorados = new ArrayList<>();

    @ManyToMany(mappedBy = "listaEstudiantes")
    private List<GrupoEstudio> gruposEstudio = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudAyuda> solicitudesAyuda = new ArrayList<>();

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<Contenido> contenidosPublicados = new ArrayList<>();

    // Estructuras propias para la lógica de negocio
    @Transient
    private ListaEnlazada<GrupoEstudio> gruposEstudioEnlazada = new ListaEnlazada<>();

    @Transient
    private ListaEnlazada<SolicitudAyuda> solicitudesAyudaEnlazada = new ListaEnlazada<>();

    @Transient
    private ListaEnlazada<Contenido> contenidosPublicadosEnlazada = new ListaEnlazada<>();

    @Transient
    private RedSocial red_social;

    public Estudiante(String username, String email, String password) {
        super(username, password, email);
    }

    // Métodos de sincronización
    private void sincronizarAPersistencia() {
        // Actualiza las listas estándar desde las enlazadas
        this.gruposEstudio = new ArrayList<>();
        this.solicitudesAyuda = new ArrayList<>();
        this.contenidosPublicados = new ArrayList<>();

        for (GrupoEstudio grupo : gruposEstudioEnlazada) {
            this.gruposEstudio.add(grupo);
        }

        for (SolicitudAyuda solicitud : solicitudesAyudaEnlazada) {
            this.solicitudesAyuda.add(solicitud);
        }

        for (Contenido contenido : contenidosPublicadosEnlazada) {
            this.contenidosPublicados.add(contenido);
        }
    }

    private void sincronizarAEnlazadas() {
        // Actualiza las listas enlazadas desde las estándar
        this.gruposEstudioEnlazada = new ListaEnlazada<>();
        this.solicitudesAyudaEnlazada = new ListaEnlazada<>();
        this.contenidosPublicadosEnlazada = new ListaEnlazada<>();

        this.gruposEstudio.forEach(gruposEstudioEnlazada::agregar);
        this.solicitudesAyuda.forEach(solicitudesAyudaEnlazada::agregar);
        this.contenidosPublicados.forEach(contenidosPublicadosEnlazada::agregar);
    }

    // Métodos de negocio (usando listas enlazadas)
    public void publicarContenido(Contenido contenido) {
        sincronizarAEnlazadas();

        contenido.setAutor(this);
        contenidosPublicadosEnlazada.agregar(contenido);

        if (this.red_social != null) {
            this.red_social.agregarPublicacion(contenido);
        }

        sincronizarAPersistencia();
    }

    public void publicarSolicitudAyuda(SolicitudAyuda solicitudAyuda) {
        sincronizarAEnlazadas();

        solicitudAyuda.setEstudiante(this);
        solicitudesAyudaEnlazada.agregar(solicitudAyuda);

        if (this.red_social != null) {
            this.red_social.agregarSolicitudAyuda(solicitudAyuda);
        }

        sincronizarAPersistencia();
    }

    public void valorarContenido(Contenido contenido) {
        if (!contenidosValorados.contains(contenido)) {
            contenidosValorados.add(contenido);
        }
        contenido.agregarLike();
    }

    // Métodos de acceso
    public ListaEnlazada<Contenido> getContenidosPublicadosEnlazada() {
        sincronizarAEnlazadas();
        return contenidosPublicadosEnlazada;
    }

    public ListaEnlazada<SolicitudAyuda> getSolicitudesAyudaEnlazada() {
        sincronizarAEnlazadas();
        return solicitudesAyudaEnlazada;
    }

    public ListaEnlazada<GrupoEstudio> getGruposEstudioEnlazada() {
        sincronizarAEnlazadas();
        return gruposEstudioEnlazada;
    }

    @Override
    public int compareTo(Estudiante otro) {
        return getUsername().compareToIgnoreCase(otro.getUsername());
    }
}