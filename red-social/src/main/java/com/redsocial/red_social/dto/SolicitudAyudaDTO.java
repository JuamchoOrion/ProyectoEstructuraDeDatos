package com.redsocial.red_social.dto;

import com.redsocial.red_social.model.Intereses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudAyudaDTO {
    private Long id;
    private Long estudianteId;
    private String estudianteUsername;
    private Date fechaNecesidad;
    private String peticion;
    private Intereses interes;
    private List<Long> gruposIds; // IDs de los grupos asociados
}