package com.clarityunited.quadtrivia.client.opentrivia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenTriviaResult Record Tests")
class OpenTriviaQuestionTest {

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
            OpenTriviaQuestion result = RESULT;

            // Assert
            assertThat(result.category()).isEqualTo("General");
            assertThat(result.type()).isEqualTo("multiple");
            assertThat(result.difficulty()).isEqualTo("easy");
            assertThat(result.question()).isEqualTo("Question?");
            assertThat(result.correctAnswer()).isEqualTo("Correct");
            assertThat(result.incorrectAnswers()).containsExactly("Wrong1", "Wrong2", "Wrong3");
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            OpenTriviaQuestion first = RESULT;
            OpenTriviaQuestion second = new OpenTriviaQuestion(
                    "General",
                    "multiple",
                    "easy",
                    "Question?",
                    "Correct",
                    List.of("Wrong1", "Wrong2", "Wrong3")
            );

            // Act & Assert
            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("Jackson Mapping")
    class JacksonMapping {

        @Test
        @DisplayName("Should deserialize snake case answer fields")
        void shouldDeserializeSnakeCaseAnswerFields() throws Exception {
            // Arrange
            String json = """
                    {
                      "category": "General",
                      "type": "multiple",
                      "difficulty": "easy",
                      "question": "Question?",
                      "correct_answer": "Correct",
                      "incorrect_answers": ["Wrong1", "Wrong2", "Wrong3"]
                    }
                    """;

            // Act
            OpenTriviaQuestion result = OBJECT_MAPPER.readValue(json, OpenTriviaQuestion.class);

            // Assert
            assertThat(result.correctAnswer()).isEqualTo("Correct");
            assertThat(result.incorrectAnswers()).containsExactly("Wrong1", "Wrong2", "Wrong3");
        }
    }
}
