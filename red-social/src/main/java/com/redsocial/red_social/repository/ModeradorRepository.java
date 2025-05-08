package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Moderador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeradorRepository extends JpaRepository<Moderador, Long> {

    Optional<Moderador> findByUsername(String username);
    Optional<Moderador> findByEmail(String email);
}