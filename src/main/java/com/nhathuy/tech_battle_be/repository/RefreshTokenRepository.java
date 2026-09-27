package com.nhathuy.tech_battle_be.repository;

import com.nhathuy.tech_battle_be.model.RefreshToken;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    List<RefreshToken> findAllByUserId(UUID userId);
}
