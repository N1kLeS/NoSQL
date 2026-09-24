package ru.nikles.lab1.history;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ru.nikles.lab1.user.RiakUnavailableException;

@Repository
public class RiakUserActionRepository {

    private static final String ACTIONS_BUCKET = "user-actions";
    private static final String USER_ID_INDEX = "user_id_bin";
    private static final String USER_ID_INDEX_HEADER = "X-Riak-Index-" + USER_ID_INDEX;

    private final RestClient restClient;

    public RiakUserActionRepository(RestClient.Builder restClientBuilder,
                                    @Value("${riak.base-url}") String riakBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(riakBaseUrl).build();
    }

    public UserAction save(UserAction action) {
        try {
            restClient.put()
                    .uri("/buckets/{bucket}/keys/{key}", ACTIONS_BUCKET, action.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(USER_ID_INDEX_HEADER, action.userId())
                    .body(action)
                    .retrieve()
                    .toBodilessEntity();
            return action;
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось сохранить действие пользователя в Riak KV", exception);
        }
    }

    public List<UserAction> findByUserId(String userId) {
        try {
            IndexQueryResult result = restClient.get()
                    .uri("/buckets/{bucket}/index/{index}/{value}",
                            ACTIONS_BUCKET, USER_ID_INDEX, userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(IndexQueryResult.class);

            if (result == null || result.keys() == null) {
                return List.of();
            }

            return result.keys().stream()
                    .map(this::findById)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (HttpClientErrorException.NotFound exception) {
            return List.of();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить историю пользователя из Riak KV", exception);
        }
    }

    private Optional<UserAction> findById(String actionId) {
        try {
            UserAction action = restClient.get()
                    .uri("/buckets/{bucket}/keys/{key}", ACTIONS_BUCKET, actionId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(UserAction.class);
            return Optional.ofNullable(action);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить действие пользователя из Riak KV", exception);
        }
    }

    private record IndexQueryResult(List<String> keys) {
    }
}
