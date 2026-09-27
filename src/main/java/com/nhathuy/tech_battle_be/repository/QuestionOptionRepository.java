package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.model.QuestionOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {

    List<QuestionOption> findAllByQuestion_IdOrderByDisplayOrderAscIdAsc(UUID questionId);

    Optional<QuestionOption> findByIdAndQuestion_Id(UUID optionId, UUID questionId);
}