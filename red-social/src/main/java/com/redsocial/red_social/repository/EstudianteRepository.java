package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    @EntityGraph(attributePaths = {"intereses"})
    Optional<Estudiante> findByUsername(String username);

    @EntityGraph(attributePaths = {"intereses"})
    Optional<Estudiante> findWithInteresesById(Long id);

    @EntityGraph(attributePaths = {"intereses"})
    List<Estudiante> findAll();

    @EntityGraph(attributePaths = {"intereses"})
    @Query("SELECT e FROM Estudiante e")
    List<Estudiante> findAllWithIntereses();
}
