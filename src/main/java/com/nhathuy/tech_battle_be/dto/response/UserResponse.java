package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.UserRole;
import com.nhathuy.tech_battle_be.common.enums.UserStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        UserRole role,
        UserStatus status
) {
}
