package com.nhathuy.tech_battle_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.nhathuy.tech_battle_be.repository")
public class RedisConfig {
}
