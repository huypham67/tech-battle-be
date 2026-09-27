package com.nhathuy.tech_battle_be.controller;

import com.nhathuy.tech_battle_be.dto.request.UpdateProfileRequest;
import com.nhathuy.tech_battle_be.dto.response.ApiResult;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;
import com.nhathuy.tech_battle_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResult<UserResponse> getCurrentProfile() {
        return ApiResult.of(HttpStatus.OK, "Profile retrieved successfully", userService.getCurrentProfile());
    }

    @PatchMapping("/me")
    public ApiResult<UserResponse> updateCurrentProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResult.of(HttpStatus.OK, "Profile updated successfully", userService.updateCurrentProfile(request));
    }
}
