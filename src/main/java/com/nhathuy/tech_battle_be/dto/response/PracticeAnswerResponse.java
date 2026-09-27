package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PracticeAnswerResponse(
        UUID sessionId,
        UUID questionId,
        boolean correct,
        Integer scoreEarned,
        String explanation,
        Integer currentQuestionIndex,
        SessionStatus status,
        PracticeQuestionResponse nextQuestion
) {
}