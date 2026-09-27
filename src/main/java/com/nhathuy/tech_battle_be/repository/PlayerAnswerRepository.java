package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.model.PlayerAnswer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, UUID> {

    List<PlayerAnswer> findAllBySession_IdAndUser_Id(UUID sessionId, UUID userId);

    Optional<PlayerAnswer> findBySessionQuestion_IdAndUser_Id(UUID sessionQuestionId, UUID userId);
}