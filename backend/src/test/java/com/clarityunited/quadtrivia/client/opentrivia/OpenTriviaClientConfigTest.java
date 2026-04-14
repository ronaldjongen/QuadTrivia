package com.clarityunited.quadtrivia.client.opentrivia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenTriviaClientConfig Tests")
class OpenTriviaClientConfigTest {

    private final OpenTriviaClientConfig config = new OpenTriviaClientConfig();

    @Test
    @DisplayName("Should return provided timeout value")
    void shouldReturnProvidedTimeoutValue() {
        // Arrange
        Duration configuredTimeout = Duration.ofSeconds(5);

        // Act
        Duration timeout = config.openTriviaRequestTimeout(configuredTimeout);

        // Assert
        assertThat(timeout).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Should create web client using provided base URL and timeout")
    void shouldCreateWebClientUsingProvidedBaseUrlAndTimeout() {
        // Arrange
        String baseUrl = "https://example.org";
        Duration timeout = Duration.ofSeconds(2);

        // Act
        WebClient webClient = config.openTriviaWebClient(baseUrl, timeout);

        // Assert
        assertThat(webClient).isNotNull();
    }
}
