package ru.nikles.lab1.session;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ru.nikles.lab1.user.UserService;

@Service
public class SessionService {

    private final RiakSessionRepository sessionRepository;
    private final UserService userService;

    public SessionService(RiakSessionRepository sessionRepository,
                          UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    public UserSession create(CreateSessionRequest request) {
        userService.findById(request.userId());

        Instant createdAt = Instant.now();
        UserSession session = new UserSession(
                UUID.randomUUID().toString(),
                request.userId(),
                createdAt,
                createdAt.plusSeconds(request.ttlSeconds())
        );
        return sessionRepository.save(session);
    }

    public UserSession findById(String sessionId) {
        UserSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        if (!Instant.now().isBefore(session.expiresAt())) {
            sessionRepository.delete(sessionId);
            throw new SessionExpiredException(sessionId);
        }

        return session;
    }
}
