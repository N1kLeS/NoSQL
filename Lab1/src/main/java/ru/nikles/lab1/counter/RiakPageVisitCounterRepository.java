package ru.nikles.lab1.counter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ru.nikles.lab1.user.RiakUnavailableException;

@Repository
public class RiakPageVisitCounterRepository {

    private static final String COUNTER_BUCKET_TYPE = "page-visit-counters";
    private static final String COUNTER_BUCKET = "page-visits";

    private final RestClient restClient;

    public RiakPageVisitCounterRepository(RestClient.Builder restClientBuilder,
                                          @Value("${riak.base-url}") String riakBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(riakBaseUrl).build();
    }

    public void increment(String pageKey) {
        try {
            restClient.post()
                    .uri("/types/{type}/buckets/{bucket}/datatypes/{key}",
                            COUNTER_BUCKET_TYPE, COUNTER_BUCKET, pageKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("increment", 1))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось увеличить счётчик посещений в Riak KV", exception);
        }
    }

    public long getValue(String pageKey) {
        try {
            CounterResponse response = restClient.get()
                    .uri("/types/{type}/buckets/{bucket}/datatypes/{key}",
                            COUNTER_BUCKET_TYPE, COUNTER_BUCKET, pageKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CounterResponse.class);
            return response == null ? 0 : response.value();
        } catch (HttpClientErrorException.NotFound exception) {
            return 0;
        } catch (RestClientException exception) {
            throw new RiakUnavailableException("Не удалось получить счётчик посещений из Riak KV", exception);
        }
    }

    private record CounterResponse(String type, long value) {
    }
}
