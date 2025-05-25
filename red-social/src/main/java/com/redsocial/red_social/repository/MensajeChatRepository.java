package com.redsocial.red_social.repository;

import com.redsocial.red_social.model.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {
    List<MensajeChat> findByEmisorIdAndReceptorIdOrReceptorIdAndEmisorId(Long e1, Long r1, Long e2, Long r2);
}