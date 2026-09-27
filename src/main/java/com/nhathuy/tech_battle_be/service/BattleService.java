package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.request.CreateBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.JoinBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.SetBattleReadyRequest;
import com.nhathuy.tech_battle_be.dto.response.BattleRoomResponse;
import java.util.UUID;

public interface BattleService {

    BattleRoomResponse createRoom(CreateBattleRoomRequest request);

    BattleRoomResponse getRoom(UUID sessionId);

    BattleRoomResponse joinRoom(JoinBattleRoomRequest request);

    BattleRoomResponse setReady(UUID sessionId, SetBattleReadyRequest request);

    void leaveRoom(UUID sessionId);

    BattleRoomResponse startRoom(UUID sessionId);

    BattleRoomResponse cancelRoom(UUID sessionId);
}
