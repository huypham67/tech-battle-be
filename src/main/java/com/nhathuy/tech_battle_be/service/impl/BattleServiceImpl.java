package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.PlayerRole;
import com.nhathuy.tech_battle_be.common.enums.QuestionStatus;
import com.nhathuy.tech_battle_be.common.enums.SessionMode;
import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import com.nhathuy.tech_battle_be.common.utils.SecurityUtils;
import com.nhathuy.tech_battle_be.dto.request.CreateBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.JoinBattleRoomRequest;
import com.nhathuy.tech_battle_be.dto.request.SetBattleReadyRequest;
import com.nhathuy.tech_battle_be.dto.response.BattlePlayerResponse;
import com.nhathuy.tech_battle_be.dto.response.BattleRoomResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.model.GameSession;
import com.nhathuy.tech_battle_be.model.Question;
import com.nhathuy.tech_battle_be.model.SessionPlayer;
import com.nhathuy.tech_battle_be.model.SessionQuestion;
import com.nhathuy.tech_battle_be.model.Topic;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.GameSessionRepository;
import com.nhathuy.tech_battle_be.repository.QuestionRepository;
import com.nhathuy.tech_battle_be.repository.SessionPlayerRepository;
import com.nhathuy.tech_battle_be.repository.SessionQuestionRepository;
import com.nhathuy.tech_battle_be.repository.TopicRepository;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import com.nhathuy.tech_battle_be.service.BattleRoomEventPublisher;
import com.nhathuy.tech_battle_be.service.BattleService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {

    private static final String SESSION_CODE_PREFIX = "BTL-";
    private static final int SESSION_CODE_RANDOM_LENGTH = 6;

    private final GameSessionRepository gameSessionRepository;
    private final QuestionRepository questionRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final BattleRoomEventPublisher battleRoomEventPublisher;

    @Override
    @Transactional
    public BattleRoomResponse createRoom(CreateBattleRoomRequest request) {
        User user = findCurrentUser();
        Topic topic = topicRepository.findByIdAndStatus(request.topicId(), TopicStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND));
        List<Question> questions = findQuestions(topic, request);
        Instant createdAt = Instant.now();

        GameSession session = gameSessionRepository.save(GameSession.builder()
                .sessionCode(generateSessionCode())
                .mode(SessionMode.BATTLE)
                .createdBy(user)
                .topic(topic)
                .difficulty(request.difficulty())
                .questionCount(request.questionCount())
                .timePerQuestion(request.timePerQuestion())
                .maxPlayers(request.maxPlayers())
                .visibility(request.visibility())
                .status(SessionStatus.WAITING)
                .build());

        List<SessionQuestion> sessionQuestions = new ArrayList<>();
        for (int index = 0; index < request.questionCount(); index++) {
            sessionQuestions.add(SessionQuestion.builder()
                    .session(session)
                    .question(questions.get(index))
                    .questionOrder(index + 1)
                    .build());
        }
        sessionQuestionRepository.saveAll(sessionQuestions);

        sessionPlayerRepository.save(SessionPlayer.builder()
                .session(session)
                .user(user)
                .role(PlayerRole.HOST)
                .joinedAt(createdAt)
                .build());

        BattleRoomResponse response = toResponse(session);
        battleRoomEventPublisher.publish("ROOM_CREATED", response, user.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BattleRoomResponse getRoom(UUID sessionId) {
        GameSession session = findBattleRoom(sessionId);
        findCurrentPlayer(session.getId());
        return toResponse(session);
    }

    @Override
    @Transactional
    public BattleRoomResponse joinRoom(JoinBattleRoomRequest request) {
        User user = findCurrentUser();
        String sessionCode = request.sessionCode().trim().toUpperCase();
        GameSession session = gameSessionRepository.findBySessionCodeAndMode(sessionCode, SessionMode.BATTLE)
                .orElseThrow(() -> new AppException(ErrorCode.BATTLE_ROOM_NOT_FOUND));

        if (session.getStatus() != SessionStatus.WAITING) {
            throw new AppException(ErrorCode.BATTLE_ROOM_NOT_WAITING);
        }

        SessionPlayer existingPlayer = sessionPlayerRepository
                .findBySession_IdAndUser_Id(session.getId(), user.getId())
                .orElse(null);
        boolean joined = existingPlayer == null;
        if (existingPlayer == null) {
            List<SessionPlayer> players = sessionPlayerRepository
                    .findAllBySession_IdOrderByJoinedAtAsc(session.getId());
            if (players.size() >= session.getMaxPlayers()) {
                throw new AppException(ErrorCode.BATTLE_ROOM_FULL);
            }

            sessionPlayerRepository.save(SessionPlayer.builder()
                    .session(session)
                    .user(user)
                    .role(PlayerRole.PLAYER)
                    .joinedAt(Instant.now())
                    .build());
        }

        BattleRoomResponse response = toResponse(session);
        if (joined) {
            battleRoomEventPublisher.publish("PLAYER_JOINED", response, user.getId());
        }
        return response;
    }

    @Override
    @Transactional
    public BattleRoomResponse setReady(UUID sessionId, SetBattleReadyRequest request) {
        GameSession session = findBattleRoom(sessionId);
        assertWaiting(session);
        SessionPlayer player = findCurrentPlayer(session.getId());
        player.setReady(request.ready());
        player.setReadyAt(request.ready() ? Instant.now() : null);
        sessionPlayerRepository.save(player);
        BattleRoomResponse response = toResponse(session);
        battleRoomEventPublisher.publish(
            request.ready() ? "PLAYER_READY" : "PLAYER_UNREADY",
            response,
            player.getUser().getId()
        );
        return response;
    }

    @Override
    @Transactional
    public void leaveRoom(UUID sessionId) {
        GameSession session = findBattleRoom(sessionId);
        assertWaiting(session);
        SessionPlayer player = findCurrentPlayer(session.getId());
        if (player.getRole() == PlayerRole.HOST) {
            throw new AppException(ErrorCode.BATTLE_HOST_CANNOT_LEAVE);
        }
        UUID actorUserId = player.getUser().getId();
        sessionPlayerRepository.delete(player);
        sessionPlayerRepository.flush();
        battleRoomEventPublisher.publish("PLAYER_LEFT", toResponse(session), actorUserId);
    }

    @Override
    @Transactional
    public BattleRoomResponse startRoom(UUID sessionId) {
        GameSession session = findBattleRoom(sessionId);
        assertWaiting(session);
        SessionPlayer currentPlayer = findCurrentPlayer(session.getId());
        assertHost(currentPlayer);

        List<SessionPlayer> players = sessionPlayerRepository
                .findAllBySession_IdOrderByJoinedAtAsc(session.getId());
        if (players.size() < 2) {
            throw new AppException(ErrorCode.BATTLE_NOT_ENOUGH_PLAYERS);
        }
        if (players.stream().anyMatch(player -> !player.isReady())) {
            throw new AppException(ErrorCode.BATTLE_PLAYERS_NOT_READY);
        }

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(Instant.now());
        gameSessionRepository.save(session);
        BattleRoomResponse response = toResponse(session);
        battleRoomEventPublisher.publish("BATTLE_STARTED", response, currentPlayer.getUser().getId());
        return response;
    }

    @Override
    @Transactional
    public BattleRoomResponse cancelRoom(UUID sessionId) {
        GameSession session = findBattleRoom(sessionId);
        assertWaiting(session);
        SessionPlayer currentPlayer = findCurrentPlayer(session.getId());
        assertHost(currentPlayer);

        session.setStatus(SessionStatus.CANCELLED);
        session.setFinishedAt(Instant.now());
        gameSessionRepository.save(session);
        BattleRoomResponse response = toResponse(session);
        battleRoomEventPublisher.publish("ROOM_CANCELLED", response, currentPlayer.getUser().getId());
        return response;
    }

    private List<Question> findQuestions(Topic topic, CreateBattleRoomRequest request) {
        List<Question> questions = questionRepository.findTop50ByTopic_IdAndDifficultyAndStatusOrderByIdAsc(
                topic.getId(),
                request.difficulty(),
                QuestionStatus.PUBLISHED
        );
        if (questions.size() < request.questionCount()) {
            throw new AppException(ErrorCode.NO_QUESTIONS_AVAILABLE);
        }
        return questions;
    }

    private String generateSessionCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String randomPart = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, SESSION_CODE_RANDOM_LENGTH)
                    .toUpperCase();
            String sessionCode = SESSION_CODE_PREFIX + randomPart;
            if (gameSessionRepository.findBySessionCodeAndMode(sessionCode, SessionMode.BATTLE).isEmpty()) {
                return sessionCode;
            }
        }
        throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Could not generate a unique battle room code");
    }

    private GameSession findBattleRoom(UUID sessionId) {
        return gameSessionRepository.findById(sessionId)
                .filter(session -> session.getMode() == SessionMode.BATTLE)
                .orElseThrow(() -> new AppException(ErrorCode.BATTLE_ROOM_NOT_FOUND));
    }

    private SessionPlayer findCurrentPlayer(UUID sessionId) {
        User user = findCurrentUser();
        return sessionPlayerRepository.findBySession_IdAndUser_Id(sessionId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.BATTLE_PLAYER_NOT_FOUND));
    }

    private User findCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void assertWaiting(GameSession session) {
        if (session.getStatus() != SessionStatus.WAITING) {
            throw new AppException(ErrorCode.BATTLE_ROOM_NOT_WAITING);
        }
    }

    private void assertHost(SessionPlayer player) {
        if (player.getRole() != PlayerRole.HOST) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private BattleRoomResponse toResponse(GameSession session) {
        List<BattlePlayerResponse> players = sessionPlayerRepository
                .findAllBySession_IdOrderByJoinedAtAsc(session.getId())
                .stream()
                .map(player -> BattlePlayerResponse.builder()
                        .userId(player.getUser().getId())
                        .displayName(player.getUser().getDisplayName() == null
                                ? player.getUser().getEmail()
                                : player.getUser().getDisplayName())
                        .avatarUrl(player.getUser().getAvatarUrl())
                        .role(player.getRole())
                        .ready(player.isReady())
                        .finalScore(player.getFinalScore())
                        .finalRank(player.getFinalRank())
                        .build())
                .toList();

        return BattleRoomResponse.builder()
                .id(session.getId())
                .sessionCode(session.getSessionCode())
                .topicId(session.getTopic().getId())
                .topicName(session.getTopic().getName())
                .difficulty(session.getDifficulty())
                .questionCount(session.getQuestionCount())
                .timePerQuestion(session.getTimePerQuestion())
                .maxPlayers(session.getMaxPlayers())
                .visibility(session.getVisibility())
                .status(session.getStatus())
                .createdBy(session.getCreatedBy().getId())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .players(players)
                .build();
    }
}
