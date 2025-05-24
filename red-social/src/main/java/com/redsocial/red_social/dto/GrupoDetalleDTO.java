package com.redsocial.red_social.dto;
import java.util.*;
import com.redsocial.red_social.model.Intereses;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDetalleDTO {
    private Long id;
    private Intereses interes;
    private List<EstudianteDTO> miembros;
    private List<ContenidoDTO> contenidos;
    private List<SolicitudAyudaDTO> solicitudes;
}