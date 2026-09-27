package com.nhathuy.tech_battle_be.dto.response;

import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TopicTreeResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String iconKey,
        TopicStatus status,
        UUID parentId,
        List<TopicTreeResponse> children
) {
    public TopicTreeResponse {
        children = children == null ? new ArrayList<>() : new ArrayList<>(children);
    }
}
