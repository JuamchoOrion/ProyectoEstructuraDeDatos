package com.redsocial.red_social.dto;

import lombok.Data;

import java.util.List;
@Data
public class CaminoDTO {
    private String estudianteA;
    private String estudianteB;
    private List<String> camino;
    private int longitud;
}
