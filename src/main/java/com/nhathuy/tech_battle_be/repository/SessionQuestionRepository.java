package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.model.SessionQuestion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, UUID> {

    List<SessionQuestion> findAllBySession_IdOrderByQuestionOrderAsc(UUID sessionId);

    Optional<SessionQuestion> findBySession_IdAndQuestion_Id(UUID sessionId, UUID questionId);

    long countBySession_Id(UUID sessionId);
}