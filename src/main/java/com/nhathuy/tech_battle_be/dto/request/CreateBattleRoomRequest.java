package com.nhathuy.tech_battle_be.dto.request;

import com.nhathuy.tech_battle_be.common.enums.Difficulty;
import com.nhathuy.tech_battle_be.common.enums.SessionVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBattleRoomRequest(

        @NotNull(message = "Topic id is required")
        UUID topicId,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @NotNull(message = "Question count is required")
        @Min(value = 1, message = "Question count must be at least 1")
        @Max(value = 50, message = "Question count must not exceed 50")
        Integer questionCount,

        @NotNull(message = "Time per question is required")
        @Min(value = 5, message = "Time per question must be at least 5 seconds")
        @Max(value = 300, message = "Time per question must not exceed 300 seconds")
        Integer timePerQuestion,

        @NotNull(message = "Maximum players is required")
        @Min(value = 2, message = "A battle must allow at least 2 players")
        @Max(value = 16, message = "Maximum players must not exceed 16")
        Integer maxPlayers,

        @NotNull(message = "Visibility is required")
        SessionVisibility visibility
) {
}
