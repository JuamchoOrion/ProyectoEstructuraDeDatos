package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    // Consultas personalizadas si las necesitas
}
