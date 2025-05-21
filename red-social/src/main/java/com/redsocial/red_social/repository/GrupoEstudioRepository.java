package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.GrupoEstudio;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, Long> {
    // Método para buscar grupos que contengan a un estudiante
    List<GrupoEstudio> findByListaEstudiantesContaining(Estudiante estudiante);
}