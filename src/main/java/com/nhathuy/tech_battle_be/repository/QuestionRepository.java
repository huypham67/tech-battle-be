package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.common.enums.Difficulty;
import com.nhathuy.tech_battle_be.common.enums.QuestionStatus;
import com.nhathuy.tech_battle_be.model.Question;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findTop50ByTopic_IdAndDifficultyAndStatusOrderByIdAsc(
            UUID topicId,
            Difficulty difficulty,
            QuestionStatus status
    );
}