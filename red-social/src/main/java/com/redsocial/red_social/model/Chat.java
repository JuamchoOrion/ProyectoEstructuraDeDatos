package com.redsocial.red_social.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.List;
import java.util.Queue;

@Data

public class Chat {
    private List<Estudiante> estudiantes;
    private Queue<String> mensajes;

    public Chat() {
    }
}
