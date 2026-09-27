package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.common.enums.SessionMode;
import com.nhathuy.tech_battle_be.model.GameSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    Optional<GameSession> findByIdAndCreatedBy_IdAndMode(UUID id, UUID userId, SessionMode mode);
}