package com.clarityunited.quadtrivia.dto.quizanswer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SubmittedAnswerDto Record Tests")
class SubmittedAnswerDtoTest {

    private static final String QUESTION_ID = "q-1";
    private static final String OPTION_ID = "o-1";

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            SubmittedAnswerDto dto = new SubmittedAnswerDto(QUESTION_ID, OPTION_ID);

            // Assert
            assertThat(dto.questionId()).isEqualTo(QUESTION_ID);
            assertThat(dto.optionId()).isEqualTo(OPTION_ID);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            SubmittedAnswerDto dto1 = new SubmittedAnswerDto(QUESTION_ID, OPTION_ID);
            SubmittedAnswerDto dto2 = new SubmittedAnswerDto(QUESTION_ID, OPTION_ID);

            // Act & Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            SubmittedAnswerDto dto1 = new SubmittedAnswerDto(QUESTION_ID, OPTION_ID);
            SubmittedAnswerDto dto2 = new SubmittedAnswerDto("other", OPTION_ID);

            // Act & Assert
            assertThat(dto1).isNotEqualTo(dto2);
            assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            SubmittedAnswerDto dto = new SubmittedAnswerDto(QUESTION_ID, OPTION_ID);

            // Act
            String toStringResult = dto.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("SubmittedAnswerDto")
                    .contains("questionId=" + QUESTION_ID)
                    .contains("optionId=" + OPTION_ID);
        }
    }
}
