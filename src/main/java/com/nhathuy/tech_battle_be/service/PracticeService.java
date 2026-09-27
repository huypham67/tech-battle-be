package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.request.CreatePracticeSessionRequest;
import com.nhathuy.tech_battle_be.dto.request.SubmitPracticeAnswerRequest;
import com.nhathuy.tech_battle_be.dto.response.PracticeAnswerResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeResultResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeSessionResponse;
import java.util.UUID;

public interface PracticeService {

    PracticeSessionResponse createSession(CreatePracticeSessionRequest request);

    PracticeSessionResponse getSession(UUID sessionId);

    PracticeAnswerResponse submitAnswer(UUID sessionId, SubmitPracticeAnswerRequest request);

    PracticeResultResponse getResult(UUID sessionId);
}