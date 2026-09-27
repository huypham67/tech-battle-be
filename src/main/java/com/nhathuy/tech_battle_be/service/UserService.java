package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.request.UpdateProfileRequest;
import com.nhathuy.tech_battle_be.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentProfile();

    UserResponse updateCurrentProfile(UpdateProfileRequest request);
}
