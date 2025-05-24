package com.redsocial.red_social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String rol;
}
