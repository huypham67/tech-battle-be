package com.nhathuy.tech_battle_be.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubmitPracticeAnswerRequest(

        @NotNull(message = "Question id is required")
        UUID questionId,

        @NotNull(message = "Option id is required")
        UUID optionId,

        @NotNull(message = "Answer time is required")
        @Min(value = 0, message = "Answer time must not be negative")
        @Max(value = 300000, message = "Answer time is too large")
        Integer answerTimeMs
) {
}