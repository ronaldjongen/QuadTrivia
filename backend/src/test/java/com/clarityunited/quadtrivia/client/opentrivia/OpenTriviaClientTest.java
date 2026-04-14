package com.clarityunited.quadtrivia.client.opentrivia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("OpenTriviaClient Tests")
class OpenTriviaClientTest {

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject amount smaller than allowed minimum")
        void shouldRejectAmountSmallerThanAllowedMinimum() {
            // Arrange
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            OpenTriviaClient client = createClient(request -> Mono.error(new RuntimeException("not used")), cache,
                    Duration.ofSeconds(1));

            // Act & Assert
            assertThatThrownBy(() -> client.fetchQuestions(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 1 and 50");
        }

        @Test
        @DisplayName("Should reject amount greater than allowed maximum")
        void shouldRejectAmountGreaterThanAllowedMaximum() {
            // Arrange
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            OpenTriviaClient client = createClient(request -> Mono.error(new RuntimeException("not used")), cache,
                    Duration.ofSeconds(1));

            // Act & Assert
            assertThatThrownBy(() -> client.fetchQuestions(51))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 1 and 50");
        }
    }

    @Nested
    @DisplayName("API Success")
    class ApiSuccess {

        @Test
        @DisplayName("Should return API response and store it in cache when response code is zero")
        void shouldReturnApiResponseAndStoreInCacheWhenResponseCodeIsZero() {
            // Arrange
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            ExchangeFunction exchangeFunction = request -> Mono.just(jsonResponse(200, """
                    {"response_code":0,"results":[{"category":"General","type":"multiple","difficulty":"easy","question":"Q?","correct_answer":"A","incorrect_answers":["B","C","D"]}]}
                    """));
            OpenTriviaClient client = createClient(exchangeFunction, cache, Duration.ofSeconds(1));

            // Act & Assert
            StepVerifier.create(client.fetchQuestions(10))
                    .expectNextMatches(response -> response.responseCode() == 0 && response.results().size() == 1)
                    .verifyComplete();

            verify(cache).put(eq(10), any(OpenTriviaResponse.class));
            verify(cache, never()).get(anyInt());
        }
    }

    @Nested
    @DisplayName("Fallback Behavior")
    class FallbackBehavior {

        @Test
        @DisplayName("Should use cache when API returns non-success response code")
        void shouldUseCacheWhenApiReturnsNonSuccessResponseCode() {
            // Arrange
            OpenTriviaResponse cached = dummyResponse(0, "Cached Question?");
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            when(cache.get(5)).thenReturn(Optional.of(cached));

            ExchangeFunction exchangeFunction = request -> Mono.just(jsonResponse(200, """
                    {"response_code":1,"results":[]}
                    """));
            OpenTriviaClient client = createClient(exchangeFunction, cache, Duration.ofSeconds(1));

            // Act & Assert
            StepVerifier.create(client.fetchQuestions(5))
                    .expectNext(cached)
                    .verifyComplete();

            verify(cache, never()).put(anyInt(), any(OpenTriviaResponse.class));
            verify(cache).get(5);
        }

        @Test
        @DisplayName("Should use cache when API call fails")
        void shouldUseCacheWhenApiCallFails() {
            // Arrange
            OpenTriviaResponse cached = dummyResponse(0, "Cached Question?");
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            when(cache.get(8)).thenReturn(Optional.of(cached));

            ExchangeFunction exchangeFunction = request -> Mono.error(new RuntimeException("API down"));
            OpenTriviaClient client = createClient(exchangeFunction, cache, Duration.ofSeconds(1));

            // Act & Assert
            StepVerifier.create(client.fetchQuestions(8))
                    .expectNext(cached)
                    .verifyComplete();

            verify(cache).get(8);
        }

        @Test
        @DisplayName("Should use cache when API call times out")
        void shouldUseCacheWhenApiCallTimesOut() {
            // Arrange
            OpenTriviaResponse cached = dummyResponse(0, "Cached Question?");
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            when(cache.get(6)).thenReturn(Optional.of(cached));

            ExchangeFunction exchangeFunction = request -> Mono.never();
            OpenTriviaClient client = createClient(exchangeFunction, cache, Duration.ofMillis(20));

            // Act & Assert
            StepVerifier.create(client.fetchQuestions(6))
                    .expectNext(cached)
                    .verifyComplete();

            verify(cache).get(6);
        }

        @Test
        @DisplayName("Should fail when API is unavailable and cache has no matching entry")
        void shouldFailWhenApiIsUnavailableAndCacheHasNoMatchingEntry() {
            // Arrange
            OpenTriviaCache cache = mock(OpenTriviaCache.class);
            when(cache.get(4)).thenReturn(Optional.empty());

            ExchangeFunction exchangeFunction = request -> Mono.error(new RuntimeException("API down"));
            OpenTriviaClient client = createClient(exchangeFunction, cache, Duration.ofSeconds(1));

            // Act & Assert
            StepVerifier.create(client.fetchQuestions(4))
                    .expectErrorMatches(error -> error instanceof RuntimeException
                            && error.getMessage().contains("No questions available from API or Cache"))
                    .verify();
        }
    }

    private OpenTriviaClient createClient(ExchangeFunction exchangeFunction, OpenTriviaCache cache, Duration timeout) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://opentdb.com")
                .exchangeFunction(exchangeFunction)
                .build();
        return new OpenTriviaClient(webClient, cache, timeout);
    }

    private ClientResponse jsonResponse(int status, String body) {
        return ClientResponse.create(HttpStatus.valueOf(status))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build();
    }

    private OpenTriviaResponse dummyResponse(int responseCode, String question) {
        OpenTriviaQuestion result = new OpenTriviaQuestion(
                "General Knowledge",
                "multiple",
                "easy",
                question,
                "Correct",
                List.of("Wrong 1", "Wrong 2", "Wrong 3")
        );
        return new OpenTriviaResponse(responseCode, List.of(result));
    }
}
