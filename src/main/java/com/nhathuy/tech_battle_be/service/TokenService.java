package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.response.TokenResponse;
import com.nhathuy.tech_battle_be.model.User;
import java.util.UUID;

public interface TokenService {

    TokenResponse issueTokens(User user);

    User rotateRefreshToken(String refreshTokenValue);

    void revokeAllTokens(UUID userId);
}
