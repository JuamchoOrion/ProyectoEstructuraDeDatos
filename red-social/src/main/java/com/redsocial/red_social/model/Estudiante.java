package com.redsocial.red_social.model;

import com.redsocial.red_social.model.estructuras.ListaEnlazada;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("ESTUDIANTE")
@NoArgsConstructor
@Getter
@Setter
public class Estudiante extends Usuario {

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "contenido_valorado",  // nombre de la tabla de unión
            joinColumns = @JoinColumn(name = "estudiante_id"),   // columna que hace referencia a la entidad Estudiante
            inverseJoinColumns = @JoinColumn(name = "contenido_id")  // columna que hace referencia a la entidad Contenido
    )
    private List<Contenido> contenidosValorados;


    @ManyToMany(mappedBy = "listaEstudiantes")
    private List<GrupoEstudio> gruposEstudio;
    @Transient
    private RedSocial red_social;

    @Transient
    private ListaEnlazada<SolicitudAyuda> solicitudesAyuda = new ListaEnlazada<>();

    @Transient
    private ListaEnlazada<Contenido> contenidosPublicados = new ListaEnlazada<>();

    @Transient
    private ListaEnlazada<Chat> chats = new ListaEnlazada<>();

    public Estudiante(String username, String email, String password) {
        super(username, password);
        this.email = email;
    }

    public void publicarContenido(Contenido contenido) {
        contenidosPublicados.agregar(contenido);
        this.red_social.agregarPublicacion(contenido);
    }

    public void valorarContenido(Contenido contenido) {
        contenido.agregarLike();
    }

    public <T> Contenido buscarContenido(T data) {
        return null;
    }
}
