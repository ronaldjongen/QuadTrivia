package com.clarityunited.quadtrivia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Password Hash Generator Test")
class PasswordHashGeneratorTest {

    @Test
    @DisplayName("Should generate a valid BCrypt hash for the demo password")
    void shouldGenerateValidBcryptHashForDemoPassword() {
        String rawPassword = "demo123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode(rawPassword);
        System.out.println("BCrypt hash for '" + rawPassword + "': " + hash);

        assertThat(hash).startsWith("$2");
        assertThat(encoder.matches(rawPassword, hash)).isTrue();
    }
}
