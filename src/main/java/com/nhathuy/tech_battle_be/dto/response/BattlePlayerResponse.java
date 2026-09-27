package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.PlayerRole;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BattlePlayerResponse(
        UUID userId,
        String displayName,
        String avatarUrl,
        PlayerRole role,
        boolean ready,
        Integer finalScore,
        Integer finalRank
) {
}
