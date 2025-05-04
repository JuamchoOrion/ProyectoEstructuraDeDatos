package com.redsocial.red_social.model;

import com.redsocial.red_social.model.estructuras.ListaEnlazada;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ESTUDIANTE") // Valor para la columna 'tipo' de la jerarquía de herencia
@NoArgsConstructor
public class Estudiante extends Usuario {

    private String email;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private ListaEnlazada<SolicitudAyuda> solicitudesAyuda;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private ListaEnlazada<Contenido> contenidosPublicados;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private ListaEnlazada<Chat> chats;

    public Estudiante(String nombre, String contrasenia, String email) {
        super(nombre, contrasenia);
        this.email = email;
        this.contenidosPublicados = new ListaEnlazada<>();
        this.solicitudesAyuda = new ListaEnlazada<>();
        this.chats = new ListaEnlazada<>();
    }

    public void publicarContenid(Contenido contenido) {
        // Método pendiente
    }

    public void valorarContenido(Contenido contenido) {
        // Método pendiente
    }

    public Contenido buscarContenidp(T data) {
        // Método pendiente
        return null;
    }
}
