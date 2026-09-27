package com.nhathuy.tech_battle_be.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BattleRoomEvent(
        String type,
        BattleRoomResponse room,
        UUID actorUserId,
        Instant occurredAt
) {
}
