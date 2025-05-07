package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MODERADOR")
@NoArgsConstructor // Constructor sin argumentos (requerido por JPA)
public class Moderador extends Usuario {
    @Transient
    private RedSocial red_social;
    public Moderador(String nombre, String contrasenia) {
        super(nombre, contrasenia);
    }
}
