package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.Difficulty;
import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PracticeSessionResponse(
        UUID id,
        UUID topicId,
        String topicName,
        Difficulty difficulty,
        Integer questionCount,
        Integer currentQuestionIndex,
        SessionStatus status,
        Instant startedAt,
        Instant finishedAt,
        PracticeQuestionResponse currentQuestion
) {
}