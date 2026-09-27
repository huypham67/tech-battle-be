package com.nhathuy.tech_battle_be.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JoinBattleRoomRequest(

        @NotBlank(message = "Session code is required")
        String sessionCode
) {
}
