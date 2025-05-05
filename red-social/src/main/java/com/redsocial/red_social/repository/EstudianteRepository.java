package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByUsername(String username);
    Optional<Estudiante> findByEmail(String email);
}
