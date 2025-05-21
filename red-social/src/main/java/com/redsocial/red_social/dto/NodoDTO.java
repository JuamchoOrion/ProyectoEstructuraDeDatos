package com.redsocial.red_social.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NodoDTO {
    private Long id;
    private String username;
    private int grado; // Número de conexiones
}