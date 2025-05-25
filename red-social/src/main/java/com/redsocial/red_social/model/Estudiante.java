package com.redsocial.red_social.model;

import com.redsocial.red_social.model.estructuras.ListaEnlazada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<SolicitudAyuda> solicitudesAyuda = new ArrayList<>();

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<Contenido> contenidosPublicados = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<Valoracion> valoraciones = new ArrayList<>();


    @ElementCollection(fetch = FetchType.EAGER) // Cambiado a EAGER
    @CollectionTable(name = "estudiante_intereses",
            joinColumns = @JoinColumn(name = "estudiante_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "interes")
    private Set<Intereses> intereses = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "amistades",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    private Set<Estudiante> amigos = new HashSet<>();

    // Estructura para lógica de negocio (transient)
    @Transient
    private ListaEnlazada<Estudiante> amigosEnlazada = new ListaEnlazada<>();

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

    public void sincronizarAPersistencia() {
        // En lugar de crear nuevas instancias, limpia las existentes
        this.gruposEstudio.clear();
        this.solicitudesAyuda.clear();
        this.contenidosPublicados.clear();
        this.amigos.clear();

        // Agrega los elementos desde las listas enlazadas
        for (GrupoEstudio grupo : gruposEstudioEnlazada) {
            this.gruposEstudio.add(grupo);
        }

        for (SolicitudAyuda solicitud : solicitudesAyudaEnlazada) {
            this.solicitudesAyuda.add(solicitud);
        }

        for (Contenido contenido : contenidosPublicadosEnlazada) {
            this.contenidosPublicados.add(contenido);
        }

        for (Estudiante estudiante : amigosEnlazada) {
            this.amigos.add(estudiante);
        }
    }

    public void sincronizarAEnlazadas() {
        // Actualiza las listas enlazadas desde las estándar
        this.gruposEstudioEnlazada = new ListaEnlazada<>();
        this.solicitudesAyudaEnlazada = new ListaEnlazada<>();
        this.contenidosPublicadosEnlazada = new ListaEnlazada<>();
        this.amigosEnlazada = new ListaEnlazada<>();
        this.gruposEstudio.forEach(gruposEstudioEnlazada::agregar);
        this.solicitudesAyuda.forEach(solicitudesAyudaEnlazada::agregar);
        this.contenidosPublicados.forEach(contenidosPublicadosEnlazada::agregar);
        this.amigos.forEach(amigosEnlazada::agregar);
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
    public void agregarInteres(Intereses interes){
        if (!intereses.contains(interes)) {
            intereses.add(interes);
        }
    }
    // Métodos para manejar amistades
    public void agregarAmigo(Estudiante amigo) {
        // Verificación simple para evitar recursión
        if (!this.amigos.contains(amigo) && !amigo.getAmigos().contains(this)) {
            this.amigos.add(amigo);
            amigo.getAmigos().add(this);
        }
    }

    public void eliminarAmigo(Estudiante amigo) {
        // Elimina directamente de la colección JPA (evitando recursión)
        this.amigos.remove(amigo);
        amigo.getAmigos().remove(this);

        // Opcional: Sincroniza solo si es necesario
        if(this.amigosEnlazada.contiene(amigo)) {
            this.amigosEnlazada.eliminar(amigo);
        }
    }
    public boolean esAmigoDe(Estudiante otroEstudiante) {
        sincronizarAEnlazadas();
        return amigosEnlazada.contiene(otroEstudiante);
    }

    public ListaEnlazada<Estudiante> getAmigosEnlazada() {
        sincronizarAEnlazadas();
        return amigosEnlazada;
    }
    @Override
    public int compareTo(Estudiante otro) {
        return getUsername().compareToIgnoreCase(otro.getUsername());
    }
}