package com.redsocial.red_social.model;

import jakarta.persistence.Entity;

import java.io.File;
import java.util.Date;

@Entity
public class Contenido {
    private Date fechaPublicacion;
    private File archivo;
}
