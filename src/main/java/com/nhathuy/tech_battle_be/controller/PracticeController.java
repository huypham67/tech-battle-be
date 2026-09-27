package com.nhathuy.tech_battle_be.controller;

import com.nhathuy.tech_battle_be.dto.request.CreatePracticeSessionRequest;
import com.nhathuy.tech_battle_be.dto.request.SubmitPracticeAnswerRequest;
import com.nhathuy.tech_battle_be.dto.response.ApiResult;
import com.nhathuy.tech_battle_be.dto.response.PracticeAnswerResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeResultResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeSessionResponse;
import com.nhathuy.tech_battle_be.service.PracticeService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/practice/sessions")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping
    public ApiResult<PracticeSessionResponse> createSession(
            @Valid @RequestBody CreatePracticeSessionRequest request
    ) {
        return ApiResult.of(
                HttpStatus.CREATED,
                "Practice session created successfully",
                practiceService.createSession(request)
        );
    }

    @GetMapping("/{sessionId}")
    public ApiResult<PracticeSessionResponse> getSession(@PathVariable UUID sessionId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Practice session retrieved successfully",
                practiceService.getSession(sessionId)
        );
    }

    @PostMapping("/{sessionId}/answers")
    public ApiResult<PracticeAnswerResponse> submitAnswer(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitPracticeAnswerRequest request
    ) {
        return ApiResult.of(
                HttpStatus.OK,
                "Practice answer submitted successfully",
                practiceService.submitAnswer(sessionId, request)
        );
    }

    @GetMapping("/{sessionId}/result")
    public ApiResult<PracticeResultResponse> getResult(@PathVariable UUID sessionId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Practice result retrieved successfully",
                practiceService.getResult(sessionId)
        );
    }
}