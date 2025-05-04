package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor // Constructor sin argumentos (requerido por JPA)
public class Moderador extends Usuario {

    @ManyToOne // Asumiendo que muchos moderadores pueden pertenecer a una RedSocial
    private RedSocial redSocial;

    public Moderador(String nombre, String contrasenia) {
        super(nombre, contrasenia);
    }
}
