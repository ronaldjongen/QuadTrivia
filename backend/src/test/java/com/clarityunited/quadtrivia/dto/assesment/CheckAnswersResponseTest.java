package com.clarityunited.quadtrivia.dto.assesment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CheckAnswersResponse Record Tests")
class CheckAnswersResponseTest {

    private static final int SCORE = 2;
    private static final int TOTAL = 3;
    private static final List<AnswerAssessmentResultDto> RESULTS = List.of(
            new AnswerAssessmentResultDto("q-1", true)
    );

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            CheckAnswersResponse response = new CheckAnswersResponse(SCORE, TOTAL, RESULTS);

            // Assert
            assertThat(response.score()).isEqualTo(SCORE);
            assertThat(response.total()).isEqualTo(TOTAL);
            assertThat(response.results()).isEqualTo(RESULTS);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            CheckAnswersResponse res1 = new CheckAnswersResponse(SCORE, TOTAL, RESULTS);
            CheckAnswersResponse res2 = new CheckAnswersResponse(SCORE, TOTAL, RESULTS);

            // Act & Assert
            assertThat(res1).isEqualTo(res2);
            assertThat(res1.hashCode()).isEqualTo(res2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            CheckAnswersResponse res1 = new CheckAnswersResponse(SCORE, TOTAL, RESULTS);
            CheckAnswersResponse res2 = new CheckAnswersResponse(0, TOTAL, RESULTS);

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
            CheckAnswersResponse response = new CheckAnswersResponse(SCORE, TOTAL, RESULTS);

            // Act
            String toStringResult = response.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("CheckAnswersResponse")
                    .contains("score=" + SCORE)
                    .contains("total=" + TOTAL)
                    .contains("results=" + RESULTS);
        }
    }
}
