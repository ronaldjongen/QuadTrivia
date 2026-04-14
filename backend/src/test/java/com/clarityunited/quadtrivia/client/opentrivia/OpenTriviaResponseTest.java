package com.clarityunited.quadtrivia.client.opentrivia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenTriviaResponse Record Tests")
class OpenTriviaResponseTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final OpenTriviaQuestion RESULT = new OpenTriviaQuestion(
            "General",
            "multiple",
            "easy",
            "Question?",
            "Correct",
            List.of("Wrong1", "Wrong2", "Wrong3")
    );

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldCorrectlyStoreAndRetrieveValues() {
            // Arrange & Act
            OpenTriviaResponse response = new OpenTriviaResponse(0, List.of(RESULT));

            // Assert
            assertThat(response.responseCode()).isEqualTo(0);
            assertThat(response.results()).containsExactly(RESULT);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            OpenTriviaResponse first = new OpenTriviaResponse(0, List.of(RESULT));
            OpenTriviaResponse second = new OpenTriviaResponse(0, List.of(RESULT));

            // Act & Assert
            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("Jackson Mapping")
    class JacksonMapping {

        @Test
        @DisplayName("Should deserialize snake case API payload")
        void shouldDeserializeSnakeCaseApiPayload() throws Exception {
            // Arrange
            String json = """
                    {
                      "response_code": 0,
                      "results": [
                        {
                          "category": "General",
                          "type": "multiple",
                          "difficulty": "easy",
                          "question": "Question?",
                          "correct_answer": "Correct",
                          "incorrect_answers": ["Wrong1", "Wrong2", "Wrong3"]
                        }
                      ]
                    }
                    """;

            // Act
            OpenTriviaResponse response = OBJECT_MAPPER.readValue(json, OpenTriviaResponse.class);

            // Assert
            assertThat(response.responseCode()).isEqualTo(0);
            assertThat(response.results()).hasSize(1);
            assertThat(response.results().get(0).correctAnswer()).isEqualTo("Correct");
        }
    }
}
