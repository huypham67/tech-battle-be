package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import com.nhathuy.tech_battle_be.model.Topic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    List<Topic> findAllByStatusOrderByNameAsc(TopicStatus status);

    Optional<Topic> findByIdAndStatus(UUID id, TopicStatus status);
}
