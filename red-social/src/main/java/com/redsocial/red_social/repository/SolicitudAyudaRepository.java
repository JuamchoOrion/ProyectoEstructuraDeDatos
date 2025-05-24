package com.redsocial.red_social.repository;


import com.redsocial.red_social.model.SolicitudAyuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitudAyudaRepository extends JpaRepository<SolicitudAyuda, Long> {

    @Query("SELECT s FROM SolicitudAyuda s ORDER BY " +
            "ABS(s.fechaNecesidad - CURRENT_TIMESTAMP) ASC")
    List<SolicitudAyuda> findAllByProximidad();
    @Query("SELECT s FROM SolicitudAyuda s WHERE s.estudiante.username = :username ORDER BY s.fechaNecesidad ASC")
    List<SolicitudAyuda> findUrgentesByUsername(@Param("username") String username);

    Optional<SolicitudAyuda> findByIdSolicitudAyuda(Long idSolicitudAyuda);

}