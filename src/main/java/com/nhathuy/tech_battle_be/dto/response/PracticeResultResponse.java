package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PracticeResultResponse(
        UUID sessionId,
        UUID topicId,
        String topicName,
        Integer totalQuestions,
        Integer answeredQuestions,
        Integer correctAnswers,
        Integer wrongAnswers,
        Integer unansweredQuestions,
        Integer totalScore,
        Double accuracy,
        SessionStatus status,
        Instant startedAt,
        Instant finishedAt
) {
}