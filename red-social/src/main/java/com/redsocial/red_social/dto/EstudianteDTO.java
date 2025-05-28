package com.redsocial.red_social.dto;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Intereses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {
    private Long id;
    private String username;
    private String email;
    private Set<Intereses> intereses;
    private Long publicaciones;
    private String password;

    // Constructor con 3 parámetros (opcional)
    public EstudianteDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.intereses = new HashSet<>(); // Inicializa el conjunto vacío
    }
}