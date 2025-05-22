package com.redsocial.red_social.service;


import com.redsocial.red_social.dto.SolicitudRequestDTO;
import com.redsocial.red_social.dto.SolicitudResponseDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.SolicitudAyuda;
import com.redsocial.red_social.repository.EstudianteRepository;
import com.redsocial.red_social.repository.SolicitudAyudaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudAyudaService {

    private final SolicitudAyudaRepository solicitudRepository;
    private final EstudianteRepository estudianteRepository;
    private final SolicitudAyudaRepository solicitudAyudaRepository;

    public SolicitudResponseDTO crearSolicitud(String username, SolicitudRequestDTO requestDTO) {
        Estudiante estudiante = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        SolicitudAyuda solicitud = new SolicitudAyuda();
        solicitud.setFechaNecesidad(requestDTO.getFechaNecesidad());
        solicitud.setPeticion(requestDTO.getPeticion());
        solicitud.setInteres(requestDTO.getInteres());
        solicitud.setEstudiante(estudiante);

        SolicitudAyuda saved = solicitudRepository.save(solicitud);

        return convertirAResponseDTO(saved);
    }

    public List<SolicitudAyuda> obtenerSolicitudesUrgentes(String username) {
        return solicitudAyudaRepository.findUrgentesByUsername(username);
    }
    private SolicitudResponseDTO convertirAResponseDTO(SolicitudAyuda solicitud) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(solicitud.getIdSolicitudAyuda());
        dto.setFechaNecesidad(solicitud.getFechaNecesidad());
        dto.setPeticion(solicitud.getPeticion());
        dto.setInteres(solicitud.getInteres());
        dto.setNombreEstudiante(solicitud.getEstudiante().getUsername());
        return dto;
    }
}