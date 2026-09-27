package com.nhathuy.tech_battle_be.service;

import com.nhathuy.tech_battle_be.dto.response.TopicResponse;
import com.nhathuy.tech_battle_be.dto.response.TopicTreeResponse;
import java.util.List;
import java.util.UUID;

public interface TopicService {

    List<TopicResponse> getTopics();

    List<TopicTreeResponse> getTopicTree();

    TopicResponse getById(UUID topicId);
}
