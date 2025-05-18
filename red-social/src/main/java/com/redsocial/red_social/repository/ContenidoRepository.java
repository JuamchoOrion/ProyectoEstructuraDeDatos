package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    List<Contenido> findByAutor(Estudiante autor);
    @Query("SELECT DISTINCT c FROM Contenido c LEFT JOIN FETCH c.valoraciones")
    List<Contenido> findAllWithValoraciones();
    List<Contenido> findAll();
}
