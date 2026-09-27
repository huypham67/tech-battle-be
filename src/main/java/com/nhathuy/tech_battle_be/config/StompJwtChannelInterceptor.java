package com.nhathuy.tech_battle_be.config;

import com.nhathuy.tech_battle_be.common.enums.TokenType;
import com.nhathuy.tech_battle_be.repository.SessionPlayerRepository;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ROOM_TOPIC_PREFIX = "/topic/battle/rooms/";

    private final JwtDecoder jwtDecoder;
    private final SessionPlayerRepository sessionPlayerRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class
        );
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (command == StompCommand.CONNECT) {
            authenticate(accessor);
        } else if (command != StompCommand.DISCONNECT
                && accessor.getUser() == null) {
            throw new MessageDeliveryException("WebSocket authentication is required");
        }

        if (command == StompCommand.SUBSCRIBE) {
            authorizeRoomSubscription(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new MessageDeliveryException("A bearer token is required to connect");
        }

        try {
            Jwt jwt = jwtDecoder.decode(authorization.substring("Bearer ".length()).trim());
            if (!TokenType.ACCESS.name().equals(jwt.getClaimAsString(TOKEN_TYPE_CLAIM))) {
                throw new MessageDeliveryException("An access token is required to connect");
            }

            accessor.setUser(toAuthentication(jwt));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new MessageDeliveryException(null, "WebSocket authentication failed");
        }
    }

    private Authentication toAuthentication(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        List<SimpleGrantedAuthority> authorities = role == null || role.isBlank()
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(jwt.getSubject(), jwt, authorities);
    }

    private void authorizeRoomSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(ROOM_TOPIC_PREFIX)) {
            return;
        }

        UUID sessionId = parseRoomId(destination.substring(ROOM_TOPIC_PREFIX.length()));
        UUID userId = parseUserId(accessor.getUser());
        if (!sessionPlayerRepository.existsBySession_IdAndUser_Id(sessionId, userId)) {
            throw new MessageDeliveryException("You are not a player in this battle room");
        }
    }

    private UUID parseRoomId(String value) {
        try {
            if (value.isBlank() || value.contains("/")) {
                throw new IllegalArgumentException();
            }
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new MessageDeliveryException("Invalid battle room subscription");
        }
    }

    private UUID parseUserId(Principal principal) {
        if (principal == null) {
            throw new MessageDeliveryException("WebSocket authentication is required");
        }
        try {
            return UUID.fromString(principal.getName());
        } catch (IllegalArgumentException exception) {
            throw new MessageDeliveryException("Invalid WebSocket principal");
        }
    }
}
