package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.UserRole;
import com.nhathuy.tech_battle_be.common.enums.UserStatus;
import com.nhathuy.tech_battle_be.common.utils.SecurityUtils;
import com.nhathuy.tech_battle_be.dto.request.LoginRequest;
import com.nhathuy.tech_battle_be.dto.request.RefreshTokenRequest;
import com.nhathuy.tech_battle_be.dto.request.RegisterRequest;
import com.nhathuy.tech_battle_be.dto.response.TokenResponse;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.mapper.UserMapper;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import com.nhathuy.tech_battle_be.service.AuthService;
import com.nhathuy.tech_battle_be.service.TokenService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-SERVICE")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .status(UserStatus.ACTIVE)
                .userRole(UserRole.USER)
                .build();

        user = userRepository.save(user);
        log.info("User registered: userId={}, email={}", user.getId(), user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = (User) authentication.getPrincipal();

        log.info("User login succeeded: userId={}, email={}", user.getId(), user.getEmail());
        return tokenService.issueTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        User user = tokenService.rotateRefreshToken(request.refreshToken());

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        log.info("Refresh token rotation succeeded: userId={}", user.getId());
        return tokenService.issueTokens(user);
    }

    @Override
    public void logout() {
        UUID userId = SecurityUtils.getCurrentUserId();
        tokenService.revokeAllTokens(userId);
        log.info("User logged out: userId={}", userId);
    }
}
