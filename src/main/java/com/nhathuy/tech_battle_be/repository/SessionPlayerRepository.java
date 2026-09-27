package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.model.SessionPlayer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionPlayerRepository extends JpaRepository<SessionPlayer, UUID> {

    Optional<SessionPlayer> findBySession_IdAndUser_Id(UUID sessionId, UUID userId);

    boolean existsBySession_IdAndUser_Id(UUID sessionId, UUID userId);

    List<SessionPlayer> findAllBySession_IdOrderByJoinedAtAsc(UUID sessionId);
}