package com.clarityunited.quadtrivia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TriviaOption Record Tests")
class TriviaOptionTest {

    private static final String OPTION_ID = "option-1";
    private static final String OPTION_TEXT = "Option text content";

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            TriviaOption option = new TriviaOption(OPTION_ID, OPTION_TEXT);

            // Assert
            assertThat(option.id()).isEqualTo(OPTION_ID);
            assertThat(option.text()).isEqualTo(OPTION_TEXT);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            TriviaOption option1 = new TriviaOption(OPTION_ID, OPTION_TEXT);
            TriviaOption option2 = new TriviaOption(OPTION_ID, OPTION_TEXT);

            // Act & Assert
            assertThat(option1).isEqualTo(option2);
            assertThat(option1.hashCode()).isEqualTo(option2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            TriviaOption option1 = new TriviaOption(OPTION_ID, OPTION_TEXT);
            TriviaOption option2 = new TriviaOption("other-id", OPTION_TEXT);

            // Act & Assert
            assertThat(option1).isNotEqualTo(option2);
            assertThat(option1.hashCode()).isNotEqualTo(option2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            TriviaOption option = new TriviaOption(OPTION_ID, OPTION_TEXT);

            // Act
            String toStringResult = option.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("TriviaOption")
                    .contains("id=" + OPTION_ID)
                    .contains("text=" + OPTION_TEXT);
        }
    }
}
