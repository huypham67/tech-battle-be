package com.nhathuy.tech_battle_be.service.impl;

import com.nhathuy.tech_battle_be.common.enums.PlayerRole;
import com.nhathuy.tech_battle_be.common.enums.QuestionStatus;
import com.nhathuy.tech_battle_be.common.enums.SessionMode;
import com.nhathuy.tech_battle_be.common.enums.SessionStatus;
import com.nhathuy.tech_battle_be.common.enums.SessionVisibility;
import com.nhathuy.tech_battle_be.common.enums.TopicStatus;
import com.nhathuy.tech_battle_be.common.utils.SecurityUtils;
import com.nhathuy.tech_battle_be.dto.request.CreatePracticeSessionRequest;
import com.nhathuy.tech_battle_be.dto.request.SubmitPracticeAnswerRequest;
import com.nhathuy.tech_battle_be.dto.response.PracticeAnswerResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeQuestionOptionResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeQuestionResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeResultResponse;
import com.nhathuy.tech_battle_be.dto.response.PracticeSessionResponse;
import com.nhathuy.tech_battle_be.exception.AppException;
import com.nhathuy.tech_battle_be.exception.ErrorCode;
import com.nhathuy.tech_battle_be.model.GameSession;
import com.nhathuy.tech_battle_be.model.PlayerAnswer;
import com.nhathuy.tech_battle_be.model.Question;
import com.nhathuy.tech_battle_be.model.QuestionOption;
import com.nhathuy.tech_battle_be.model.SessionPlayer;
import com.nhathuy.tech_battle_be.model.SessionQuestion;
import com.nhathuy.tech_battle_be.model.Topic;
import com.nhathuy.tech_battle_be.model.User;
import com.nhathuy.tech_battle_be.repository.GameSessionRepository;
import com.nhathuy.tech_battle_be.repository.PlayerAnswerRepository;
import com.nhathuy.tech_battle_be.repository.QuestionOptionRepository;
import com.nhathuy.tech_battle_be.repository.QuestionRepository;
import com.nhathuy.tech_battle_be.repository.SessionPlayerRepository;
import com.nhathuy.tech_battle_be.repository.SessionQuestionRepository;
import com.nhathuy.tech_battle_be.repository.TopicRepository;
import com.nhathuy.tech_battle_be.repository.UserRepository;
import com.nhathuy.tech_battle_be.service.PracticeService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private static final int TIME_PER_QUESTION_SECONDS = 30;
    private static final int BASE_SCORE = 1000;

    private final GameSessionRepository gameSessionRepository;
    private final PlayerAnswerRepository playerAnswerRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PracticeSessionResponse createSession(CreatePracticeSessionRequest request) {
        User user = findCurrentUser();
        Topic topic = topicRepository.findByIdAndStatus(request.topicId(), TopicStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND));

        List<Question> questions = questionRepository.findTop50ByTopic_IdAndDifficultyAndStatusOrderByIdAsc(
                topic.getId(),
                request.difficulty(),
                QuestionStatus.PUBLISHED
        );
        if (questions.size() < request.questionCount()) {
            throw new AppException(ErrorCode.NO_QUESTIONS_AVAILABLE);
        }

        Instant startedAt = Instant.now();
        GameSession session = gameSessionRepository.save(GameSession.builder()
                .mode(SessionMode.PRACTICE)
                .createdBy(user)
                .topic(topic)
                .difficulty(request.difficulty())
                .questionCount(request.questionCount())
                .timePerQuestion(TIME_PER_QUESTION_SECONDS)
                .maxPlayers(1)
                .visibility(SessionVisibility.PRIVATE)
                .status(SessionStatus.IN_PROGRESS)
                .startedAt(startedAt)
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
                .joinedAt(startedAt)
                .build());

        return toSessionResponse(session, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeSessionResponse getSession(UUID sessionId) {
        User user = findCurrentUser();
        return toSessionResponse(findPracticeSession(sessionId, user.getId()), user.getId());
    }

    @Override
    @Transactional
    public PracticeAnswerResponse submitAnswer(UUID sessionId, SubmitPracticeAnswerRequest request) {
        User user = findCurrentUser();
        GameSession session = findPracticeSession(sessionId, user.getId());
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.PRACTICE_SESSION_FINISHED);
        }

        SessionQuestion sessionQuestion = sessionQuestionRepository
                .findBySession_IdAndQuestion_Id(sessionId, request.questionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_IN_SESSION));
        if (playerAnswerRepository.findBySessionQuestion_IdAndUser_Id(sessionQuestion.getId(), user.getId()).isPresent()) {
            throw new AppException(ErrorCode.QUESTION_ALREADY_ANSWERED);
        }

        QuestionOption selectedOption = questionOptionRepository
                .findByIdAndQuestion_Id(request.optionId(), request.questionId())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_OPTION_NOT_FOUND));
        boolean correct = selectedOption.isCorrect();
        int scoreEarned = calculateScore(correct, request.answerTimeMs());
        Instant answeredAt = Instant.now();

        playerAnswerRepository.save(PlayerAnswer.builder()
                .session(session)
                .sessionQuestion(sessionQuestion)
                .user(user)
                .selectedOption(selectedOption)
                .correct(correct)
                .answerTimeMs(request.answerTimeMs())
                .scoreEarned(scoreEarned)
                .answeredAt(answeredAt)
                .build());

        List<PlayerAnswer> answers = playerAnswerRepository.findAllBySession_IdAndUser_Id(sessionId, user.getId());
        int totalQuestions = (int) sessionQuestionRepository.countBySession_Id(sessionId);
        boolean finished = answers.size() >= totalQuestions;
        if (finished) {
            session.setStatus(SessionStatus.FINISHED);
            session.setFinishedAt(answeredAt);
        }
        gameSessionRepository.save(session);
        updatePlayerStats(session, user.getId(), answers, totalQuestions, finished, answeredAt);

        PracticeQuestionResponse nextQuestion = finished
                ? null
                : findNextQuestion(session, answers);
        return PracticeAnswerResponse.builder()
                .sessionId(session.getId())
                .questionId(request.questionId())
                .correct(correct)
                .scoreEarned(scoreEarned)
                .explanation(sessionQuestion.getQuestion().getExplanation())
                .currentQuestionIndex(answers.size())
                .status(session.getStatus())
                .nextQuestion(nextQuestion)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeResultResponse getResult(UUID sessionId) {
        User user = findCurrentUser();
        GameSession session = findPracticeSession(sessionId, user.getId());
        List<PlayerAnswer> answers = playerAnswerRepository.findAllBySession_IdAndUser_Id(sessionId, user.getId());
        int totalQuestions = (int) sessionQuestionRepository.countBySession_Id(sessionId);
        int correctAnswers = (int) answers.stream().filter(PlayerAnswer::isCorrect).count();
        int answeredQuestions = answers.size();
        int wrongAnswers = answeredQuestions - correctAnswers;
        int unansweredQuestions = Math.max(0, totalQuestions - answeredQuestions);
        int totalScore = answers.stream().mapToInt(PlayerAnswer::getScoreEarned).sum();
        double accuracy = answeredQuestions == 0 ? 0.0 : (double) correctAnswers / answeredQuestions;

        return PracticeResultResponse.builder()
                .sessionId(session.getId())
                .topicId(session.getTopic().getId())
                .topicName(session.getTopic().getName())
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .correctAnswers(correctAnswers)
                .wrongAnswers(wrongAnswers)
                .unansweredQuestions(unansweredQuestions)
                .totalScore(totalScore)
                .accuracy(accuracy)
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .build();
    }

    private PracticeSessionResponse toSessionResponse(GameSession session, UUID userId) {
        List<PlayerAnswer> answers = playerAnswerRepository.findAllBySession_IdAndUser_Id(session.getId(), userId);
        PracticeQuestionResponse currentQuestion = session.getStatus() == SessionStatus.IN_PROGRESS
                ? findNextQuestion(session, answers)
                : null;
        return PracticeSessionResponse.builder()
                .id(session.getId())
                .topicId(session.getTopic().getId())
                .topicName(session.getTopic().getName())
                .difficulty(session.getDifficulty())
                .questionCount(session.getQuestionCount())
                .currentQuestionIndex(answers.size())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .currentQuestion(currentQuestion)
                .build();
    }

    private PracticeQuestionResponse findNextQuestion(GameSession session, List<PlayerAnswer> answers) {
        Set<UUID> answeredQuestionIds = answers.stream()
                .map(answer -> answer.getSessionQuestion().getQuestion().getId())
                .collect(java.util.stream.Collectors.toSet());
        return sessionQuestionRepository.findAllBySession_IdOrderByQuestionOrderAsc(session.getId()).stream()
                .filter(sessionQuestion -> !answeredQuestionIds.contains(sessionQuestion.getQuestion().getId()))
                .findFirst()
                .map(this::toQuestionResponse)
                .orElse(null);
    }

    private PracticeQuestionResponse toQuestionResponse(SessionQuestion sessionQuestion) {
        Question question = sessionQuestion.getQuestion();
        List<PracticeQuestionOptionResponse> options = questionOptionRepository
                .findAllByQuestion_IdOrderByDisplayOrderAscIdAsc(question.getId())
                .stream()
                .map(option -> PracticeQuestionOptionResponse.builder()
                        .id(option.getId())
                        .content(option.getContent())
                        .displayOrder(option.getDisplayOrder())
                        .build())
                .toList();
        return PracticeQuestionResponse.builder()
                .id(question.getId())
                .questionNumber(sessionQuestion.getQuestionOrder())
                .content(question.getContent())
                .questionType(question.getQuestionType())
                .options(options)
                .build();
    }

    private void updatePlayerStats(
            GameSession session,
            UUID userId,
            List<PlayerAnswer> answers,
            int totalQuestions,
            boolean finished,
            Instant timestamp
    ) {
        SessionPlayer player = sessionPlayerRepository.findBySession_IdAndUser_Id(session.getId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.PRACTICE_SESSION_NOT_FOUND));
        int correctAnswers = (int) answers.stream().filter(PlayerAnswer::isCorrect).count();
        player.setFinalScore(answers.stream().mapToInt(PlayerAnswer::getScoreEarned).sum());
        player.setCorrectCount(correctAnswers);
        player.setWrongCount(answers.size() - correctAnswers);
        player.setUnansweredCount(Math.max(0, totalQuestions - answers.size()));
        if (finished) {
            player.setFinishedAt(timestamp);
        }
        sessionPlayerRepository.save(player);
    }

    private int calculateScore(boolean correct, int answerTimeMs) {
        if (!correct) {
            return 0;
        }
        int maxTimeMs = TIME_PER_QUESTION_SECONDS * 1000;
        int clampedTimeMs = Math.min(Math.max(answerTimeMs, 0), maxTimeMs);
        int speedBonus = (maxTimeMs - clampedTimeMs) / 30;
        return BASE_SCORE + speedBonus;
    }

    private GameSession findPracticeSession(UUID sessionId, UUID userId) {
        return gameSessionRepository.findByIdAndCreatedBy_IdAndMode(sessionId, userId, SessionMode.PRACTICE)
                .orElseThrow(() -> new AppException(ErrorCode.PRACTICE_SESSION_NOT_FOUND));
    }

    private User findCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}