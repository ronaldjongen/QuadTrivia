package com.clarityunited.quadtrivia.dto.quiz;

import com.clarityunited.quadtrivia.model.TriviaOption;
import com.clarityunited.quadtrivia.model.TriviaQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuizResponse Record Tests")
class QuizResponseTest {

    private static final String QUIZ_ID = "quiz-123";
    private static final List<TriviaQuestion> QUESTIONS = List.of(
            new TriviaQuestion("q-1", "Text", List.of(new TriviaOption("o-1", "Opt")))
    );

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            QuizResponse response = new QuizResponse(QUIZ_ID, QUESTIONS);

            // Assert
            assertThat(response.quizId()).isEqualTo(QUIZ_ID);
            assertThat(response.questions()).isEqualTo(QUESTIONS);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            QuizResponse res1 = new QuizResponse(QUIZ_ID, QUESTIONS);
            QuizResponse res2 = new QuizResponse(QUIZ_ID, QUESTIONS);

            // Act & Assert
            assertThat(res1).isEqualTo(res2);
            assertThat(res1.hashCode()).isEqualTo(res2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            QuizResponse res1 = new QuizResponse(QUIZ_ID, QUESTIONS);
            QuizResponse res2 = new QuizResponse("other", QUESTIONS);

            // Act & Assert
            assertThat(res1).isNotEqualTo(res2);
            assertThat(res1.hashCode()).isNotEqualTo(res2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            QuizResponse response = new QuizResponse(QUIZ_ID, QUESTIONS);

            // Act
            String toStringResult = response.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("QuizResponse")
                    .contains("quizId=" + QUIZ_ID)
                    .contains("questions=" + QUESTIONS);
        }
    }
}
