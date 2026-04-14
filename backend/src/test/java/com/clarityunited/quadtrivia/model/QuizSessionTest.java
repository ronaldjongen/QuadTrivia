package com.clarityunited.quadtrivia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuizSession Record Tests")
class QuizSessionTests {

    private static final String QUIZ_ID = "quiz-123";
    private static final String USERNAME = "john_doe";
    private static final Instant CREATED_AT = Instant.now();
    private static final Map<String, String> CORRECT_ANSWERS = Map.of("q1", "a1", "q2", "a2");

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            QuizSession session = new QuizSession(QUIZ_ID, USERNAME, CREATED_AT, CORRECT_ANSWERS);

            // Assert
            assertThat(session.quizId()).isEqualTo(QUIZ_ID);
            assertThat(session.username()).isEqualTo(USERNAME);
            assertThat(session.createdAt()).isEqualTo(CREATED_AT);
            assertThat(session.correctAnswersByQuestionId()).isEqualTo(CORRECT_ANSWERS);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            QuizSession session1 = new QuizSession(QUIZ_ID, USERNAME, CREATED_AT, CORRECT_ANSWERS);
            QuizSession session2 = new QuizSession(QUIZ_ID, USERNAME, CREATED_AT, CORRECT_ANSWERS);

            // Act & Assert
            assertThat(session1).isEqualTo(session2);
            assertThat(session1.hashCode()).isEqualTo(session2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            QuizSession session1 = new QuizSession(QUIZ_ID, USERNAME, CREATED_AT, CORRECT_ANSWERS);
            QuizSession session2 = new QuizSession("other-id", USERNAME, CREATED_AT, CORRECT_ANSWERS);

            // Act & Assert
            assertThat(session1).isNotEqualTo(session2);
            assertThat(session1.hashCode()).isNotEqualTo(session2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            QuizSession session = new QuizSession(QUIZ_ID, USERNAME, CREATED_AT, CORRECT_ANSWERS);

            // Act
            String toStringResult = session.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("QuizSession")
                    .contains("quizId=" + QUIZ_ID)
                    .contains("username=" + USERNAME)
                    .contains("createdAt=" + CREATED_AT)
                    .contains("correctAnswersByQuestionId=" + CORRECT_ANSWERS);
        }
    }
}
