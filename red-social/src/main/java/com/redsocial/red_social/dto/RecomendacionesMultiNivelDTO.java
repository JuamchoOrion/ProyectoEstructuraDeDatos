package com.redsocial.red_social.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RecomendacionesMultiNivelDTO {
    private List<EstudianteDTO> primerNivel;
    private List<EstudianteDTO> segundoNivel;

    // Constructores, getters y setters
    @JsonCreator
    public RecomendacionesMultiNivelDTO(List<EstudianteDTO> primerNivel, List<EstudianteDTO> segundoNivel) {
        this.primerNivel = primerNivel;
        this.segundoNivel = segundoNivel;
    }

    // Getters y setters...
}