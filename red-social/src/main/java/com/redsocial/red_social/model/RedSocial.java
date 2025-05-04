package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name ="red_social")
@Data
public class RedSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Una red social tiene muchos usuarios
    @OneToMany(mappedBy = "redSocial", cascade = CascadeType.ALL)
    private List<Usuario> listaUsuarios;

    // Muchas solicitudes de ayuda asociadas a la red social
    @OneToMany(mappedBy = "redSocial", cascade = CascadeType.ALL)
    private List<SolicitudAyuda> solicitudAyudas;

    // Moderadores asociados a la red social
    @OneToMany(mappedBy = "redSocial", cascade = CascadeType.ALL)
    private List<Moderador> moderadores;

    // Grupos de estudio en la red social
    @OneToMany(mappedBy = "redSocial", cascade = CascadeType.ALL)
    private List<GrupoEstudio> listaGrupoEstudios;

    public RedSocial(List<Usuario> listaUsuarios, List<SolicitudAyuda> solicitudAyudas, List<Moderador> moderadores, List<GrupoEstudio> listaGrupoEstudios) {
        this.listaUsuarios = listaUsuarios;
        this.solicitudAyudas = solicitudAyudas;
        this.moderadores = moderadores;
        this.listaGrupoEstudios = listaGrupoEstudios;
    }

    public RedSocial() {
    }
}
