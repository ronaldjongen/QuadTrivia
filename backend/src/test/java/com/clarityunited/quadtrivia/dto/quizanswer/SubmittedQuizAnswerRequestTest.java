package com.clarityunited.quadtrivia.dto.quizanswer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SubmittedQuizAnswerRequest Record Tests")
class SubmittedQuizAnswerRequestTest {

    private static final String QUIZ_ID = "quiz-123";
    private static final List<SubmittedAnswerDto> ANSWERS = List.of(
            new SubmittedAnswerDto("q-1", "o-1")
    );

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(QUIZ_ID, ANSWERS);

            // Assert
            assertThat(request.quizId()).isEqualTo(QUIZ_ID);
            assertThat(request.answers()).isEqualTo(ANSWERS);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            SubmittedQuizAnswerRequest req1 = new SubmittedQuizAnswerRequest(QUIZ_ID, ANSWERS);
            SubmittedQuizAnswerRequest req2 = new SubmittedQuizAnswerRequest(QUIZ_ID, ANSWERS);

            // Act & Assert
            assertThat(req1).isEqualTo(req2);
            assertThat(req1.hashCode()).isEqualTo(req2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            SubmittedQuizAnswerRequest req1 = new SubmittedQuizAnswerRequest(QUIZ_ID, ANSWERS);
            SubmittedQuizAnswerRequest req2 = new SubmittedQuizAnswerRequest("other", ANSWERS);

            // Act & Assert
            assertThat(req1).isNotEqualTo(req2);
            assertThat(req1.hashCode()).isNotEqualTo(req2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(QUIZ_ID, ANSWERS);

            // Act
            String toStringResult = request.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("SubmittedQuizAnswerRequest")
                    .contains("quizId=" + QUIZ_ID)
                    .contains("answers=" + ANSWERS);
        }
    }
}
