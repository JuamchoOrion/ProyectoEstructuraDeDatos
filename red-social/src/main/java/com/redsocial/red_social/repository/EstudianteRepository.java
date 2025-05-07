package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Usuario> findByUsername(String username);
}
