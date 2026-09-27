package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.request.LoginRequest;
import com.nhathuy.tech_battle_be.dto.request.RefreshTokenRequest;
import com.nhathuy.tech_battle_be.dto.request.RegisterRequest;
import com.nhathuy.tech_battle_be.dto.response.TokenResponse;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse refresh(RefreshTokenRequest request);

    void logout();
}
