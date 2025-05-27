package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    // Método existente
    List<Contenido> findByAutor(Estudiante autor);

    // Método existente
    @Query("SELECT DISTINCT c FROM Contenido c LEFT JOIN FETCH c.valoraciones")
    List<Contenido> findAllWithValoraciones();


    @Query("SELECT COUNT(c) FROM Contenido c WHERE c.autor.id = :autorId")
    Long countByAutorId(@Param("autorId") Long autorId);
}
