package com.nhathuy.tech_battle_be.controller;

import com.nhathuy.tech_battle_be.dto.request.CreateBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.JoinBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.SetBattleReadyRequest;
import com.nhathuy.tech_battle_be.dto.response.ApiResult;
import com.nhathuy.tech_battle_be.dto.response.BattleRoomResponse;
import com.nhathuy.tech_battle_be.service.BattleService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battle/rooms")
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;

    @PostMapping
    public ApiResult<BattleRoomResponse> createRoom(
            @Valid @RequestBody CreateBattleRoomRequest request
    ) {
        return ApiResult.of(
                HttpStatus.CREATED,
                "Battle room created successfully",
                battleService.createRoom(request)
        );
    }

    @GetMapping("/{sessionId}")
    public ApiResult<BattleRoomResponse> getRoom(@PathVariable UUID sessionId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Battle room retrieved successfully",
                battleService.getRoom(sessionId)
        );
    }

    @PostMapping("/join")
    public ApiResult<BattleRoomResponse> joinRoom(
            @Valid @RequestBody JoinBattleRoomRequest request
    ) {
        return ApiResult.of(
                HttpStatus.OK,
                "Joined battle room successfully",
                battleService.joinRoom(request)
        );
    }

    @PostMapping("/{sessionId}/ready")
    public ApiResult<BattleRoomResponse> setReady(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SetBattleReadyRequest request
    ) {
        return ApiResult.of(
                HttpStatus.OK,
                "Battle player ready state updated successfully",
                battleService.setReady(sessionId, request)
        );
    }

    @DeleteMapping("/{sessionId}/players/me")
    public ApiResult<Void> leaveRoom(@PathVariable UUID sessionId) {
        battleService.leaveRoom(sessionId);
        return ApiResult.of(HttpStatus.OK, "Left battle room successfully", null);
    }

    @PostMapping("/{sessionId}/start")
    public ApiResult<BattleRoomResponse> startRoom(@PathVariable UUID sessionId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Battle started successfully",
                battleService.startRoom(sessionId)
        );
    }

    @PostMapping("/{sessionId}/cancel")
    public ApiResult<BattleRoomResponse> cancelRoom(@PathVariable UUID sessionId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Battle room cancelled successfully",
                battleService.cancelRoom(sessionId)
        );
    }
}
