package com.clarityunited.quadtrivia.dto.assesment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnswerAssessmentResultDto Record Tests")
class AnswerAssessmentResultDtoTest {

    private static final String QUESTION_ID = "q-1";
    private static final boolean CORRECT = true;

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            AnswerAssessmentResultDto dto = new AnswerAssessmentResultDto(QUESTION_ID, CORRECT);

            // Assert
            assertThat(dto.questionId()).isEqualTo(QUESTION_ID);
            assertThat(dto.correct()).isEqualTo(CORRECT);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            AnswerAssessmentResultDto dto1 = new AnswerAssessmentResultDto(QUESTION_ID, CORRECT);
            AnswerAssessmentResultDto dto2 = new AnswerAssessmentResultDto(QUESTION_ID, CORRECT);

            // Act & Assert
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            AnswerAssessmentResultDto dto1 = new AnswerAssessmentResultDto(QUESTION_ID, CORRECT);
            AnswerAssessmentResultDto dto2 = new AnswerAssessmentResultDto("other", CORRECT);

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
            AnswerAssessmentResultDto dto = new AnswerAssessmentResultDto(QUESTION_ID, CORRECT);

            // Act
            String toStringResult = dto.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("AnswerAssessmentResultDto")
                    .contains("questionId=" + QUESTION_ID)
                    .contains("correct=" + CORRECT);
        }
    }
}
