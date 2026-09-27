package com.nhathuy.tech_battle_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TechBattleBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechBattleBeApplication.class, args);
	}

}
