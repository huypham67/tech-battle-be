package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.TokenType;
import com.nhathuy.tech_battle_be.config.JwtProperties;
import com.nhathuy.tech_battle_be.dto.response.TokenResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.model.RefreshToken;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.RefreshTokenRepository;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import com.nhathuy.tech_battle_be.service.TokenService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TOKEN-SERVICE")
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public TokenResponse issueTokens(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public User rotateRefreshToken(String refreshTokenValue) {
        try {
            var jwt = jwtDecoder.decode(refreshTokenValue);
            String tokenType = jwt.getClaimAsString(TOKEN_TYPE_CLAIM);
            if (!TokenType.REFRESH.name().equals(tokenType)) {
                log.warn("Rejected token with invalid type: tokenType={}", tokenType);
                throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
            String jti = Objects.requireNonNull(jwt.getId());

            RefreshToken stored = refreshTokenRepository.findById(jti).orElse(null);
            boolean valid = stored != null
                    && stored.getUserId().equals(userId)
                    && stored.getTokenHash().equals(hashToken(refreshTokenValue));

            if (!valid) {
                // Missing/mismatched entry means the token was already rotated, expired, or
                // forged. Treat it as a possible theft signal and burn every active session.
                revokeAllTokens(userId);
                throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            refreshTokenRepository.delete(stored);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            log.info("Refresh token rotated: userId={}, jti={}", userId, jti);
            return user;
        } catch (JwtException | IllegalArgumentException exception) {
            log.warn("Rejected invalid refresh token: reason={}", exception.getClass().getSimpleName());
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public void revokeAllTokens(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        refreshTokenRepository.deleteAll(tokens);
        log.info("Revoked all refresh tokens: userId={}, count={}", userId, tokens.size());
    }

    private String generateAccessToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.getAccessTokenExpirySeconds()))
                .claim(TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .claim("email", user.getEmail());

        if (user.getUserRole() != null) {
            claimsBuilder.claim("role", user.getUserRole().name());
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    private String generateRefreshToken(User user) {
        Instant now = Instant.now();
        long expirySeconds = jwtProperties.getRefreshTokenExpirySeconds();
        UUID jti = UUID.randomUUID();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .id(jti.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirySeconds))
                .claim(TOKEN_TYPE_CLAIM, TokenType.REFRESH.name())
                .build();

        String refreshTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        refreshTokenRepository.save(RefreshToken.builder()
                .jti(jti.toString())
                .userId(user.getId())
                .tokenHash(hashToken(refreshTokenValue))
                .issuedAt(now)
                .ttlSeconds(expirySeconds)
                .build());

        log.debug("Refresh token issued: userId={}, jti={}", user.getId(), jti);
        return refreshTokenValue;
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
