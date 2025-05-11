package com.redsocial.red_social.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
@Service
public class FileStorageService {

    /**private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento: " + uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Validar que el archivo no esté vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo proporcionado está vacío o es nulo");
        }

        // Validar nombre de archivo
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("El nombre del archivo contiene secuencias de ruta no válidas: " + originalFileName);
        }

        // Generar nombre único para almacenamiento
        String fileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            // Validar tamaño máximo (ejemplo: 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                throw new IllegalArgumentException("El tamaño del archivo excede el límite permitido (5MB)");
            }

            // Copiar archivo al directorio de destino
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error al almacenar el archivo: " + originalFileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("El archivo no existe o no se puede leer: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error al acceder al archivo: " + fileName, ex);
        }
    }**/
}