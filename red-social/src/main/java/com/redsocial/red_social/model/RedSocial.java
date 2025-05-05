package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
public class RedSocial {


    private Long id;


    private List<Usuario> listaUsuarios;

    private List<SolicitudAyuda> solicitudAyudas;


    private List<Moderador> moderadores;

    private List<GrupoEstudio> listaGrupoEstudios;

    private List<Contenido> listaContenidos;

    public RedSocial(List<Usuario> listaUsuarios, List<SolicitudAyuda> solicitudAyudas, List<Moderador> moderadores, List<GrupoEstudio> listaGrupoEstudios) {
        this.listaUsuarios = listaUsuarios;
        this.solicitudAyudas = solicitudAyudas;
        this.moderadores = moderadores;
        this.listaGrupoEstudios = listaGrupoEstudios;
    }
    public void agregarPublicacion(Contenido contenido){
        listaContenidos.add(contenido);
    }
    public RedSocial() {
    }
}
