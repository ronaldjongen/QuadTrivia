package com.clarityunited.quadtrivia.client.opentrivia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenTriviaCache Tests")
class OpenTriviaCacheTest {

    @TempDir
    Path tempDir;

    private String cacheFilePath;
    private OpenTriviaCache cache;

    @BeforeEach
    void setUp() {
        cacheFilePath = tempDir.resolve("test-cache.yaml").toString();
        cache = new OpenTriviaCache(cacheFilePath);
    }

    @Nested
    @DisplayName("Cache Operations")
    class CacheOperations {

        @Test
        @DisplayName("Should return empty when amount does not exist")
        void shouldReturnEmptyWhenAmountDoesNotExist() {
            // Act
            Optional<OpenTriviaResponse> missing = cache.get(99);

            // Assert
            assertThat(missing).isEmpty();
        }

        @Test
        @DisplayName("Should store and retrieve response from memory")
        void shouldStoreAndRetrieveFromMemory() {
            // Arrange
            int amount = 10;
            OpenTriviaResponse response = createDummyResponse();

            // Act
            cache.put(amount, response);
            Optional<OpenTriviaResponse> retrieved = cache.get(amount);

            // Assert
            assertThat(retrieved).isPresent().contains(response);
        }

        @Test
        @DisplayName("Should persist and reload cache from file")
        void shouldPersistAndReloadFromFile() {
            // Arrange
            int amount = 5;
            OpenTriviaResponse response = createDummyResponse();
            cache.put(amount, response);

            // Act - Create a new cache instance pointing to the same file
            OpenTriviaCache newCache = new OpenTriviaCache(cacheFilePath);
            Optional<OpenTriviaResponse> retrieved = newCache.get(amount);

            // Assert
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().responseCode()).isEqualTo(response.responseCode());
            assertThat(retrieved.get().results()).hasSize(response.results().size());
        }

        @Test
        @DisplayName("Should handle invalid YAML cache file without crashing")
        void shouldHandleInvalidYamlCacheFileWithoutCrashing() throws Exception {
            // Arrange
            Files.writeString(tempDir.resolve("test-cache.yaml"), "this: is: not: valid: yaml");

            // Act
            OpenTriviaCache newCache = new OpenTriviaCache(cacheFilePath);

            // Assert
            assertThat(newCache.get(1)).isEmpty();
        }
    }

    private OpenTriviaResponse createDummyResponse() {
        OpenTriviaQuestion result = new OpenTriviaQuestion(
                "Category", "multiple", "easy", "Question?", "Correct", List.of("Wrong1", "Wrong2")
        );
        return new OpenTriviaResponse(0, List.of(result));
    }
}
