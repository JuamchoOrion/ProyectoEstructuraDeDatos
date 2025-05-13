package com.redsocial.red_social.service;

import com.redsocial.red_social.dto.ContenidoResponse;
import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.TipoContenido;
import com.redsocial.red_social.repository.ContenidoRepository;
import com.redsocial.red_social.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ContenidoService {

    @Value("${directorio.archivos}") // define esto en application.properties
    private String directorioArchivos;

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Contenido guardarContenido(MultipartFile archivo, String descripcion, String username, TipoContenido tipoContenido) throws IOException {
        Estudiante autor = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Nombre único para almacenamiento
        String nombreAlmacenado = UUID.randomUUID() + "_" + archivo.getOriginalFilename();

        // Ruta donde guardar el archivo
        Path rutaArchivo = Paths.get(directorioArchivos, nombreAlmacenado);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        // Crear objeto Contenido
        Contenido contenido = Contenido.builder()
                .autor(autor)
                .nombreOriginal(archivo.getOriginalFilename())
                .nombreAlmacenado(nombreAlmacenado)
                .tipoArchivo(archivo.getContentType())
                .tamanio(archivo.getSize())
                .descripcion(descripcion)
                .fechaPublicacion(LocalDateTime.now())
                .likes(0L)
                .tipoContenido(tipoContenido)
                .build();

        return contenidoRepository.save(contenido);
    }
    // Método para determinar el tipo de contenido basado en el archivo
    private TipoContenido determinarTipoContenido(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        String fileName = archivo.getOriginalFilename().toLowerCase();

        if (contentType.startsWith("video/")) {
            return TipoContenido.VIDEO_EDUCATIVO;
        } else if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return TipoContenido.MATERIAL_ESTUDIO;
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return TipoContenido.PRESENTACION;
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return TipoContenido.EJERCICIOS_PRACTICOS;
        }
        return TipoContenido.OTROS;
    }

    public Contenido obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con id: " + id));
    }

}
