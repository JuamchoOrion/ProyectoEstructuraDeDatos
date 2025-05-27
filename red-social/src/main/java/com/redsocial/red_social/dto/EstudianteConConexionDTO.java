package com.redsocial.red_social.dto;

import lombok.Data;

@Data
public class EstudianteConConexionDTO {
    private Long id;
    private String username;
    private String email;
    private int conexiones;

}
