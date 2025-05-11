package com.redsocial.red_social.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContenidoResponse {
    private Long id;
    private String titulo;
    private String cuerpo;
    private String autor;
    private LocalDateTime fechaPublicacion;
    private int likes;
}