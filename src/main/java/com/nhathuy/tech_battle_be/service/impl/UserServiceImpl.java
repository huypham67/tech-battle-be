package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.utils.SecurityUtils;
import com.nhathuy.tech_battle_be.dto.request.UpdateProfileRequest;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.mapper.UserMapper;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import com.nhathuy.tech_battle_be.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentProfile() {
        return userMapper.toResponse(findCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateCurrentProfile(UpdateProfileRequest request) {
        User user = findCurrentUser();

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }

        user = userRepository.save(user);
        log.info("Profile updated: userId={}", user.getId());
        return userMapper.toResponse(user);
    }

    private User findCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
