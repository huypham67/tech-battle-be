package com.nhathuy.tech_battle_be.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(400, "Validation failed", HttpStatus.BAD_REQUEST),
    INVALID_BODY(400, "Malformed or missing request body", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(400, "File type is not supported for this category", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED(400, "File size exceeds the allowable limit for this category", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD(400, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_CURRENT(400, "New password must differ from current password", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_RESET_TOKEN(400, "Invalid or expired password reset token", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(401, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(401, "Invalid or expired refresh token", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN(403, "Access denied", HttpStatus.FORBIDDEN),
    ACCOUNT_INACTIVE(403, "Account is inactive", HttpStatus.FORBIDDEN),
    ACCOUNT_BLOCKED(403, "Account is blocked", HttpStatus.FORBIDDEN),

    // 404 Not Found
    TOPIC_NOT_FOUND(404, "Topic not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    PRACTICE_SESSION_NOT_FOUND(404, "Practice session not found", HttpStatus.NOT_FOUND),
    BATTLE_ROOM_NOT_FOUND(404, "Battle room not found", HttpStatus.NOT_FOUND),
    BATTLE_PLAYER_NOT_FOUND(404, "You are not a player in this battle room", HttpStatus.NOT_FOUND),

    // 409 Conflict
    EMAIL_ALREADY_IN_USE(409, "Email is already in use", HttpStatus.CONFLICT),
    USERNAME_ALREADY_IN_USE(409, "Username is already in use", HttpStatus.CONFLICT),
    PRACTICE_SESSION_FINISHED(409, "Practice session is already finished", HttpStatus.CONFLICT),
    QUESTION_ALREADY_ANSWERED(409, "Question has already been answered", HttpStatus.CONFLICT),
    BATTLE_ROOM_NOT_WAITING(409, "Battle room is not waiting for players", HttpStatus.CONFLICT),
    BATTLE_ROOM_FULL(409, "Battle room is full", HttpStatus.CONFLICT),
    BATTLE_HOST_CANNOT_LEAVE(409, "The host must cancel the room instead of leaving it", HttpStatus.CONFLICT),
    BATTLE_PLAYERS_NOT_READY(409, "All players must be ready before the battle starts", HttpStatus.CONFLICT),

    // 422 Unprocessable Entity
    NO_QUESTIONS_AVAILABLE(422, "Not enough questions available for this topic and difficulty", HttpStatus.UNPROCESSABLE_ENTITY),
    QUESTION_NOT_IN_SESSION(422, "Question does not belong to this practice session", HttpStatus.UNPROCESSABLE_ENTITY),
    ANSWER_OPTION_NOT_FOUND(422, "Answer option does not belong to this question", HttpStatus.UNPROCESSABLE_ENTITY),
    BATTLE_NOT_ENOUGH_PLAYERS(422, "A battle needs at least two players", HttpStatus.UNPROCESSABLE_ENTITY),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
