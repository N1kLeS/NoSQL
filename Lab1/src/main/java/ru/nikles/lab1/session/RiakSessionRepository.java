package ru.nikles.lab1.session;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ru.nikles.lab1.user.RiakUnavailableException;

@Repository
public class RiakSessionRepository {

    private static final String SESSIONS_BUCKET = "sessions";

    private final RestClient restClient;

    public RiakSessionRepository(RestClient.Builder restClientBuilder,
                                 @Value("${riak.base-url}") String riakBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(riakBaseUrl).build();
    }

    public UserSession save(UserSession session) {
        try {
            restClient.put()
                    .uri("/buckets/{bucket}/keys/{key}", SESSIONS_BUCKET, session.sessionId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(session)
                    .retrieve()
                    .toBodilessEntity();
            return session;
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось сохранить пользовательскую сессию в Riak KV", exception);
        }
    }

    public Optional<UserSession> findById(String sessionId) {
        try {
            UserSession session = restClient.get()
                    .uri("/buckets/{bucket}/keys/{key}", SESSIONS_BUCKET, sessionId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(UserSession.class);
            return Optional.ofNullable(session);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить пользовательскую сессию из Riak KV", exception);
        }
    }

    public void delete(String sessionId) {
        try {
            restClient.delete()
                    .uri("/buckets/{bucket}/keys/{key}", SESSIONS_BUCKET, sessionId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ignored) {

        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось удалить пользовательскую сессию из Riak KV", exception);
        }
    }
}
