package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    boolean existsByEstudianteAndContenido(Estudiante estudiante, Contenido contenido);
    List<Valoracion> findByContenido(Contenido contenido);
    // Método para calcular el promedio de valoraciones de un contenido específico
    @Query("SELECT COALESCE(AVG(v.puntuacion), 0.0) FROM Valoracion v WHERE v.contenido = :contenido")
    Double calcularPromedioPorContenido(@Param("contenido") Contenido contenido);

    // Versión alternativa usando ID del contenido
    @Query("SELECT COALESCE(AVG(v.puntuacion), 0.0) FROM Valoracion v WHERE v.contenido.id = :contenidoId")
    Double calcularPromedioPorContenidoId(@Param("contenidoId") Long contenidoId);


    List<Valoracion> findByEstudiante(Estudiante estudiante);
}
