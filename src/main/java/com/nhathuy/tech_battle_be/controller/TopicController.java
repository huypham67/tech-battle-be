package com.nhathuy.tech_battle_be.controller;

import com.nhathuy.tech_battle_be.dto.response.ApiResult;
import com.nhathuy.tech_battle_be.dto.response.TopicResponse;
import com.nhathuy.tech_battle_be.dto.response.TopicTreeResponse;
import com.nhathuy.tech_battle_be.service.TopicService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ApiResult<List<TopicResponse>> getTopics() {
        return ApiResult.of(
                HttpStatus.OK,
                "Topics retrieved successfully",
                topicService.getTopics()
        );
    }

    @GetMapping("/tree")
    public ApiResult<List<TopicTreeResponse>> getTopicTree() {
        return ApiResult.of(
                HttpStatus.OK,
                "Topic tree retrieved successfully",
                topicService.getTopicTree()
        );
    }

    @GetMapping("/{topicId}")
    public ApiResult<TopicResponse> getById(@PathVariable UUID topicId) {
        return ApiResult.of(
                HttpStatus.OK,
                "Topic retrieved successfully",
                topicService.getById(topicId)
        );
    }
}
