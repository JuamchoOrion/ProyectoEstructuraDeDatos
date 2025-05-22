package com.redsocial.red_social.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AristaDTO {
    private Long origen;
    private Long destino;
    private int peso;
}