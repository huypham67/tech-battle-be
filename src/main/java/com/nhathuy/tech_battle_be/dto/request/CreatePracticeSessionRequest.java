package com.nhathuy.tech_battle_be.dto.request;

import com.nhathuy.tech_battle_be.common.enums.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePracticeSessionRequest(

        @NotNull(message = "Topic id is required")
        UUID topicId,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @NotNull(message = "Question count is required")
        @Min(value = 1, message = "Question count must be at least 1")
        @Max(value = 50, message = "Question count must not exceed 50")
        Integer questionCount
) {
}