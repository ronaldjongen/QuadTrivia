package com.clarityunited.quadtrivia.client.opentrivia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Component
public class OpenTriviaClient {

    private static final Logger log = LoggerFactory.getLogger(OpenTriviaClient.class);
    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 50;
    private final WebClient webClient;
    private final OpenTriviaCache cache;
    private final Duration requestTimeout;

    public OpenTriviaClient(
            @Qualifier("openTriviaWebClient") WebClient webClient,
            OpenTriviaCache cache,
            Duration openTriviaRequestTimeout
    ) {
        this.webClient = webClient;
        this.cache = cache;
        this.requestTimeout = openTriviaRequestTimeout;
    }

    /**
     * Fetches questions from OpenTrivia API.
     * If the API is unavailable, it returns a cached response if available.
     *
     * @param amount Number of questions to fetch.
     * @return OpenTriviaResponse from API or cache.
     */
    public Mono<OpenTriviaResponse> fetchQuestions(int amount) {
        validateAmount(amount);
        return callApi(amount)
                .flatMap(response -> {
                    if (response.responseCode() == 0) {
                        // Persisting to file is blocking I/O, so keep it off event-loop threads.
                        return Mono.fromRunnable(() -> cache.put(amount, response))
                                .subscribeOn(Schedulers.boundedElastic())
                                .thenReturn(response);
                    }
                    log.warn("OpenTrivia API returned non-success responseCode={} for amount={}. Falling back to cache.",
                            response.responseCode(), amount);
                    return fallbackFromCache(amount);
                })
                .onErrorResume(error -> {
                    log.warn("OpenTrivia API call failed for amount {}. Falling back to cache.", amount, error);
                    return fallbackFromCache(amount);
                });
    }

    private Mono<OpenTriviaResponse> callApi(int amount) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api.php")
                        .queryParam("amount", amount)
                        .queryParam("type", "multiple")
                        .build())
                .retrieve()
                .bodyToMono(OpenTriviaResponse.class)
                .timeout(requestTimeout);
    }

    private Mono<OpenTriviaResponse> fallbackFromCache(int amount) {
        return Mono.defer(() -> cache.get(amount)
                .map(Mono::just)
                .orElseGet(() -> Mono.error(
                        new RuntimeException("No questions available from API or Cache for amount: " + amount)
                )));
    }

    private void validateAmount(int amount) {
        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            throw new IllegalArgumentException(
                    "amount must be between %d and %d, but was: %d".formatted(MIN_AMOUNT, MAX_AMOUNT, amount)
            );
        }
    }
}
