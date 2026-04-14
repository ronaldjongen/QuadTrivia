package com.clarityunited.quadtrivia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TriviaQuestion Record Tests")
class TriviaQuestionTest {

    private static final String QUESTION_ID = "q-1";
    private static final String QUESTION_TEXT = "How are you?";
    private static final List<TriviaOption> OPTIONS = List.of(
            new TriviaOption("o-1", "Fine"),
            new TriviaOption("o-2", "Not so fine")
    );

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            TriviaQuestion question = new TriviaQuestion(QUESTION_ID, QUESTION_TEXT, OPTIONS);

            // Assert
            assertThat(question.id()).isEqualTo(QUESTION_ID);
            assertThat(question.text()).isEqualTo(QUESTION_TEXT);
            assertThat(question.options()).isEqualTo(OPTIONS);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            TriviaQuestion q1 = new TriviaQuestion(QUESTION_ID, QUESTION_TEXT, OPTIONS);
            TriviaQuestion q2 = new TriviaQuestion(QUESTION_ID, QUESTION_TEXT, OPTIONS);

            // Act & Assert
            assertThat(q1).isEqualTo(q2);
            assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            TriviaQuestion q1 = new TriviaQuestion(QUESTION_ID, QUESTION_TEXT, OPTIONS);
            TriviaQuestion q2 = new TriviaQuestion("different-id", QUESTION_TEXT, OPTIONS);

            // Act & Assert
            assertThat(q1).isNotEqualTo(q2);
            assertThat(q1.hashCode()).isNotEqualTo(q2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            TriviaQuestion question = new TriviaQuestion(QUESTION_ID, QUESTION_TEXT, OPTIONS);

            // Act
            String toStringResult = question.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("TriviaQuestion")
                    .contains("id=" + QUESTION_ID)
                    .contains("text=" + QUESTION_TEXT)
                    .contains("options=" + OPTIONS);
        }
    }
}
