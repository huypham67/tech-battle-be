package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.QuestionType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PracticeQuestionResponse(
        UUID id,
        Integer questionNumber,
        String content,
        QuestionType questionType,
        List<PracticeQuestionOptionResponse> options
) {
    public PracticeQuestionResponse {
        options = options == null ? List.of() : List.copyOf(options);
    }
}