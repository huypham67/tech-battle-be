package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TopicResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String iconUrl,
        TopicStatus status,
        UUID parentId
) {
}
