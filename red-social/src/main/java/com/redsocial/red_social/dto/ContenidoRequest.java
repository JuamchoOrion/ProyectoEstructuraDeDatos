package com.redsocial.red_social.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContenidoRequest {
    @NotBlank
    private String titulo;

    @NotBlank
    private String cuerpo;
}