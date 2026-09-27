package com.nhathuy.tech_battle_be.controller;

import com.nhathuy.tech_battle_be.dto.request.LoginRequest;
import com.nhathuy.tech_battle_be.dto.request.RefreshTokenRequest;
import com.nhathuy.tech_battle_be.dto.request.RegisterRequest;
import com.nhathuy.tech_battle_be.dto.response.ApiResult;
import com.nhathuy.tech_battle_be.dto.response.TokenResponse;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;
import com.nhathuy.tech_battle_be.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResult.of(HttpStatus.CREATED, "Registered successfully", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResult<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.of(HttpStatus.OK, "Login successful", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResult<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResult.of(HttpStatus.OK, "Token refreshed successfully", authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.of(HttpStatus.OK, "Logged out successfully", null);
    }
}
