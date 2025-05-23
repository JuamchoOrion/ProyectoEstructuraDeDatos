package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.GrupoEstudio;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, Long> {
    // Método para buscar grupos que contengan a un estudiante
    List<GrupoEstudio> findByListaEstudiantesContaining(Estudiante estudiante);
    //Metodo para buscar Un grupo de estudio dado un Interes
    Optional<GrupoEstudio> findByInteres(Intereses interes);
    @Query("SELECT g FROM GrupoEstudio g JOIN FETCH g.listaEstudiantes WHERE :estudiante MEMBER OF g.listaEstudiantes")
    List<GrupoEstudio> findByListaEstudiantesContainingFetch(@Param("estudiante") Estudiante estudiante);
}