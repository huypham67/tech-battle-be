package com.nhathuy.tech_battle_be.mapper;

import com.nhathuy.tech_battle_be.dto.response.TopicResponse;
import com.nhathuy.tech_battle_be.dto.response.TopicTreeResponse;
import com.nhathuy.tech_battle_be.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    @Mapping(target = "parentId", source = "parent.id")
    TopicResponse toResponse(Topic topic);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    TopicTreeResponse toTreeResponse(Topic topic);
}
