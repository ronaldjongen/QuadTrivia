package com.clarityunited.quadtrivia.dto.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginResult Record Tests")
class LoginResultTest {

    private static final String USERNAME = "testUser";
    private static final boolean AUTHENTICATED = true;

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            LoginResult result = new LoginResult(USERNAME, AUTHENTICATED);

            // Assert
            assertThat(result.username()).isEqualTo(USERNAME);
            assertThat(result.isAuthenticated()).isEqualTo(AUTHENTICATED);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            LoginResult result1 = new LoginResult(USERNAME, AUTHENTICATED);
            LoginResult result2 = new LoginResult(USERNAME, AUTHENTICATED);

            // Act & Assert
            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            LoginResult result1 = new LoginResult(USERNAME, AUTHENTICATED);
            LoginResult result2 = new LoginResult(USERNAME, false);

            // Act & Assert
            assertThat(result1).isNotEqualTo(result2);
            assertThat(result1.hashCode()).isNotEqualTo(result2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            LoginResult result = new LoginResult(USERNAME, AUTHENTICATED);

            // Act
            String toStringResult = result.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("LoginResult")
                    .contains("username=" + USERNAME)
                    .contains("isAuthenticated=" + AUTHENTICATED);
        }
    }
}
