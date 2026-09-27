package com.nhathuy.tech_battle_be.mapper;

import com.nhathuy.tech_battle_be.dto.response.UserResponse;
import com.nhathuy.tech_battle_be.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "userRole")
    UserResponse toResponse(User user);
}
