package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.response.BattleRoomEvent;
import com.nhathuy.tech_battle_be.dto.response.BattleRoomResponse;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleRoomEventPublisher {

    private static final String ROOM_TOPIC_PREFIX = "/topic/battle/rooms/";

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(String type, BattleRoomResponse room, UUID actorUserId) {
        messagingTemplate.convertAndSend(
                ROOM_TOPIC_PREFIX + room.id(),
                BattleRoomEvent.builder()
                        .type(type)
                        .room(room)
                        .actorUserId(actorUserId)
                        .occurredAt(Instant.now())
                        .build());
    }
}
