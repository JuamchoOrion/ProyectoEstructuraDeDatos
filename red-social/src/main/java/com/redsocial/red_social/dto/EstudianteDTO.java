package com.redsocial.red_social.dto;

import com.redsocial.red_social.model.Intereses;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class EstudianteDTO {
    private Long id;
    private String username;
    private String email;
    private Set<Intereses> intereses;
}
