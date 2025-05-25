package com.redsocial.red_social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
//@Data
//@Entity
//@Table(name = "mensaje")
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder

public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contenido;
    private Estudiante emisor;
    private Estudiante receptor;
    private LocalDateTime fecha;
    private Chat chat;

}
