package com.redsocial.red_social.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.redsocial.red_social.model.Intereses;
import lombok.Data;

import java.util.Date;

@Data
public class SolicitudRequestDTO {
    private String peticion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private Date fechaNecesidad;
    private Intereses interes;
}