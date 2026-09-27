package com.nhathuy.tech_battle_be.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PracticeQuestionOptionResponse(
        UUID id,
        String content,
        Integer displayOrder
) {
}