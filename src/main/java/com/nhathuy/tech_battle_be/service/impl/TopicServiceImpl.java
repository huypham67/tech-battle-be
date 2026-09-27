package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import com.nhathuy.tech_battle_be.dto.response.TopicResponse;
import com.nhathuy.tech_battle_be.dto.response.TopicTreeResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.mapper.TopicMapper;
import com.nhathuy.tech_battle_be.model.Topic;
import com.nhathuy.tech_battle_be.repository.TopicRepository;
import com.nhathuy.tech_battle_be.service.TopicService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TOPIC-SERVICE")
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopics() {
        return topicRepository.findAllByStatusOrderByNameAsc(TopicStatus.ACTIVE).stream()
                .map(topicMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicTreeResponse> getTopicTree() {
        List<Topic> topics = topicRepository.findAllByStatusOrderByNameAsc(TopicStatus.ACTIVE);
        Map<UUID, TopicTreeResponse> nodes = topics.stream()
                .collect(Collectors.toMap(
                        Topic::getId,
                        topicMapper::toTreeResponse,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));

        List<TopicTreeResponse> roots = new ArrayList<>();
        for (Topic topic : topics) {
            TopicTreeResponse current = nodes.get(topic.getId());
            UUID parentId = topic.getParent() == null ? null : topic.getParent().getId();

            if (parentId == null) {
                roots.add(current);
                continue;
            }

            TopicTreeResponse parent = nodes.get(parentId);
            if (parent != null) {
                parent.children().add(current);
            }
        }

        return roots;
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getById(UUID topicId) {
        Topic topic = topicRepository.findByIdAndStatus(topicId, TopicStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND));
        return topicMapper.toResponse(topic);
    }
}
