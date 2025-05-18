package com.redsocial.red_social.dto;

// ContenidoDTO.java

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.TipoContenido;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContenidoDTO {
    private Long id;
    private String nombreOriginal;
    private String tipoArchivo;
    private String descripcion;
    private LocalDateTime fechaPublicacion;
    private TipoContenido tipoContenido;
    private Long likes;
    private String autor;
    private Double promedioValoracion;
    private String url;
    private String nombreAlmacenado; // para acceder al archivo en /uploads/
}
