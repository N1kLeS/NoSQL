package ru.nikles.lab1.profile;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ru.nikles.lab1.user.RiakUnavailableException;

@Repository
public class RiakProfileCacheRepository {

    private static final String PROFILE_CACHE_BUCKET = "profile-cache";

    private final RestClient restClient;

    public RiakProfileCacheRepository(RestClient.Builder restClientBuilder,
                                      @Value("${riak.base-url}") String riakBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(riakBaseUrl).build();
    }

    public Optional<ProfileCacheEntry> findByUserId(String userId) {
        try {
            ProfileCacheEntry entry = restClient.get()
                    .uri("/buckets/{bucket}/keys/{key}", PROFILE_CACHE_BUCKET, userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ProfileCacheEntry.class);
            return Optional.ofNullable(entry);
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить профиль из кэша Riak KV", exception);
        }
    }

    public ProfileCacheEntry save(ProfileCacheEntry entry) {
        try {
            restClient.put()
                    .uri("/buckets/{bucket}/keys/{key}", PROFILE_CACHE_BUCKET, entry.userId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(entry)
                    .retrieve()
                    .toBodilessEntity();
            return entry;
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось сохранить профиль в кэш Riak KV", exception);
        }
    }

    public void delete(String userId) {
        try {
            restClient.delete()
                    .uri("/buckets/{bucket}/keys/{key}", PROFILE_CACHE_BUCKET, userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ignored) {

        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось удалить профиль из кэша Riak KV", exception);
        }
    }
}
