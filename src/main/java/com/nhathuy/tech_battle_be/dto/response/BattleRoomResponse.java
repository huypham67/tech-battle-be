package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.Difficulty;
import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import com.nhathuy.tech_battle_be.common.enums.SessionVisibility;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BattleRoomResponse(
        UUID id,
        String sessionCode,
        UUID topicId,
        String topicName,
        Difficulty difficulty,
        Integer questionCount,
        Integer timePerQuestion,
        Integer maxPlayers,
        SessionVisibility visibility,
        SessionStatus status,
        UUID createdBy,
        Instant startedAt,
        Instant finishedAt,
        List<BattlePlayerResponse> players
) {
    public BattleRoomResponse {
        players = players == null ? List.of() : List.copyOf(players);
    }
}
