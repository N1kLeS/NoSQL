package ru.nikles.lab1.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class RiakUserRepository {

    private static final String USERS_BUCKET = "users";

    private final RestClient restClient;

    public RiakUserRepository(RestClient.Builder restClientBuilder,
                              @Value("${riak.base-url}") String riakBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(riakBaseUrl).build();
    }

    public User save(User user) {
        try {
            restClient.put()
                    .uri("/buckets/{bucket}/keys/{key}", USERS_BUCKET, user.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .retrieve()
                    .toBodilessEntity();
            return user;
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось сохранить пользователя в Riak KV", exception);
        }
    }

    public Optional<User> findById(String userId) {
        try {
            User user = restClient.get()
                    .uri("/buckets/{bucket}/keys/{key}", USERS_BUCKET, userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(User.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить пользователя из Riak KV", exception);
        }
    }
}
