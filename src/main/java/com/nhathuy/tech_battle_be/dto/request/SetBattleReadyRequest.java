package com.nhathuy.tech_battle_be.dto.request;

import jakarta.validation.constraints.NotNull;

public record SetBattleReadyRequest(

        @NotNull(message = "Ready state is required")
        Boolean ready
) {
}
