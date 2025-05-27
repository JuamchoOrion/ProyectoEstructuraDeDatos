package com.redsocial.red_social.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MensajeChatDTO {
    private Long receptorId;
    private String contenido;
    private Long emisorId;
}
