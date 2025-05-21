package com.redsocial.red_social.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GrafoDTO {
    private List<NodoDTO> nodos;
    private List<AristaDTO> aristas;
}
