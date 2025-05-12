package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    List<Contenido> findByAutor(Estudiante autor);
}
