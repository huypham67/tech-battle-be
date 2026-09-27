package com.nhathuy.tech_battle_be.model;

import com.nhathuy.tech_battle_be.common.enums.PlayerRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "session_players",
    uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "user_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionPlayer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlayerRole role;

    @Column(name = "final_score", nullable = false)
    @Builder.Default
    private Integer finalScore = 0;

    @Column(name = "correct_count", nullable = false)
    @Builder.Default
    private Integer correctCount = 0;

    @Column(name = "wrong_count", nullable = false)
    @Builder.Default
    private Integer wrongCount = 0;

    @Column(name = "unanswered_count", nullable = false)
    @Builder.Default
    private Integer unansweredCount = 0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean ready = false;

    @Column(name = "ready_at")
    private Instant readyAt;

    @Column(name = "final_rank")
    private Integer finalRank;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

}
