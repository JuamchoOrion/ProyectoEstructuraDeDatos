package com.redsocial.red_social.service;

import com.redsocial.red_social.dto.ContenidoResponse;
import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.TipoContenido;
import com.redsocial.red_social.model.estructuras.ArbolABB;
import com.redsocial.red_social.repository.ContenidoRepository;
import com.redsocial.red_social.repository.EstudianteRepository;
import jakarta.annotation.PostConstruct;
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
import java.util.List;
import java.util.UUID;

@Service
public class ContenidoService {

    @Value("${directorio.archivos}") // define esto en application.properties
    private String directorioArchivos;

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    private final ArbolABB arbolContenidos = new ArbolABB();
/**
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
    }**/
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
/**
    public Contenido obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con id: " + id));
    }**/
//NUEVOOOOOOOOOOOOOOOOOOOOOOOOOOOO
@PostConstruct  // Comenta temporalmente esta línea
public void init() {
    try {
        List<Contenido> contenidos = contenidoRepository.findAll();

        // Verificación adicional de nulos
        if(contenidos != null) {
            for(Contenido contenido : contenidos) {
                if(contenido != null && contenido.getAutor() != null) {
                    arbolContenidos.insertar(contenido);
                }
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Error al inicializar el árbol ABB", e);
    }
}

    public Contenido guardarContenido(MultipartFile archivo, String descripcion,
                                      String username, TipoContenido tipoContenido) throws IOException {
        // 1. Validaciones básicas
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username es requerido");
        }

        // 2. Obtener autor
        Estudiante autor = estudianteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 3. Generar nombre único para el archivo
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreAlmacenado = UUID.randomUUID().toString() + extension;

        // 4. Crear directorio si no existe
        Path directorio = Paths.get(directorioArchivos);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // 5. Guardar archivo físico
        Path rutaArchivo = directorio.resolve(nombreAlmacenado);
        try {
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error al guardar archivo en el sistema", e);
        }

        // 6. Crear entidad Contenido
        Contenido contenido = Contenido.builder()
                .autor(autor)
                .nombreOriginal(nombreOriginal)
                .nombreAlmacenado(nombreAlmacenado)
                .tipoArchivo(archivo.getContentType())
                .tamanio(archivo.getSize())
                .descripcion(descripcion)
                .fechaPublicacion(LocalDateTime.now())
                .likes(0L)
                .tipoContenido(tipoContenido)
                .build();

        // 7. Persistir en BD y árbol
        try {
            Contenido guardado = contenidoRepository.save(contenido);
            arbolContenidos.insertar(guardado);
            return guardado;
        } catch (Exception e) {
            // Rollback: eliminar archivo si falla la BD
            Files.deleteIfExists(rutaArchivo);
            throw new RuntimeException("Error al guardar contenido en la base de datos", e);
        }
    }

    public Contenido obtenerPorId(Long id) {
        Contenido contenido = arbolContenidos.buscarPorId(id);
        if (contenido == null) {
            contenido = contenidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));
            arbolContenidos.insertar(contenido); // Para mantener sincronizado
        }
        return contenido;
    }

    public List<Contenido> obtenerPorAutor(String autor) {
        return arbolContenidos.buscarPorAutor(autor);
    }

    public List<Contenido> obtenerTodos() {
        return arbolContenidos.obtenerTodosContenidos();
    }
}



