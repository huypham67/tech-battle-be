package com.nhathuy.tech_battle_be.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

// One document per active refresh token; TTL alone expires and evicts it from Redis.
@RedisHash("refresh_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String jti;

    @Indexed
    private UUID userId;

    private String tokenHash;

    private Instant issuedAt;

    @TimeToLive
    private long ttlSeconds;
}
