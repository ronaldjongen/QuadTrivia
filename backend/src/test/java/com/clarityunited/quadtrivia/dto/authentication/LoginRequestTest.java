package com.clarityunited.quadtrivia.dto.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRequest Record Tests")
class LoginRequestTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorAndFieldAccess {

        @Test
        @DisplayName("Should correctly store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            // Arrange & Act
            LoginRequest request = new LoginRequest(USERNAME, PASSWORD);

            // Assert
            assertThat(request.username()).isEqualTo(USERNAME);
            assertThat(request.password()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Should be equal when fields are identical")
        void shouldBeEqualWhenFieldsAreIdentical() {
            // Arrange
            LoginRequest request1 = new LoginRequest(USERNAME, PASSWORD);
            LoginRequest request2 = new LoginRequest(USERNAME, PASSWORD);

            // Act & Assert
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            LoginRequest request1 = new LoginRequest(USERNAME, PASSWORD);
            LoginRequest request2 = new LoginRequest("otherUser", PASSWORD);

            // Act & Assert
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("Should have a descriptive toString implementation")
        void shouldHaveDescriptiveToString() {
            // Arrange
            LoginRequest request = new LoginRequest(USERNAME, PASSWORD);

            // Act
            String toStringResult = request.toString();

            // Assert
            assertThat(toStringResult)
                    .contains("LoginRequest")
                    .contains("username=" + USERNAME)
                    .contains("password=" + PASSWORD);
        }
    }
}
