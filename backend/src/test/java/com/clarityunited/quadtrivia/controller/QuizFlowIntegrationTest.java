package com.clarityunited.quadtrivia.controller;

import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaClient;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaQuestion;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizFlowIntegrationTest {

    private static final String DEMO_USERNAME = "demo";
    private static final String DEMO_PASSWORD = "demo123";

    @DynamicPropertySource
    static void testProperties(DynamicPropertyRegistry registry) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        registry.add("app.security.demo-username", () -> DEMO_USERNAME);
        registry.add("app.security.demo-password-hash", () -> encoder.encode(DEMO_PASSWORD));
    }

    @TestConfiguration
    static class StubbedClientConfig {
        @Bean
        @Primary
        OpenTriviaClient openTriviaClient() {
            return Mockito.mock(OpenTriviaClient.class);
        }
    }

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OpenTriviaClient openTriviaClient;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        this.client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        when(openTriviaClient.fetchQuestions(5)).thenReturn(Mono.just(new OpenTriviaResponse(
                0,
                List.of(
                        new OpenTriviaQuestion(
                                "General",
                                "multiple",
                                "easy",
                                "Capital of France?",
                                "Paris",
                                List.of("Berlin", "Madrid", "Rome")
                        ),
                        new OpenTriviaQuestion(
                                "Math",
                                "multiple",
                                "easy",
                                "2 + 2?",
                                "4",
                                List.of("3", "5", "6")
                        )
                )
        )));
    }

    @Test
    @DisplayName("Complete authenticated quiz flow should work and avoid leaking correct option ids")
    void completeQuizFlow() throws Exception {
        EntityExchangeResult<String> csrfResponse = client.get()
                .uri("/api/auth/csrf")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        String csrfToken = csrfResponse.getResponseCookies().getFirst("XSRF-TOKEN").getValue();

        WebTestClient csrfPrimedClient = client.mutate()
                .defaultCookie("XSRF-TOKEN", csrfResponse.getResponseCookies().getFirst("XSRF-TOKEN").getValue())
                .defaultHeader("X-XSRF-TOKEN", csrfToken)
                .build();

        FluxExchangeResult<Void> loginResponse = csrfPrimedClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"demo\",\"password\":\"demo123\"}")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class);

        String sessionId = loginResponse.getResponseCookies().getFirst("JSESSIONID").getValue();
        WebTestClient authenticatedClient = csrfPrimedClient.mutate()
                .defaultCookie("JSESSIONID", sessionId)
                .build();

        EntityExchangeResult<String> questionsResponse = authenticatedClient.get()
                .uri("/api/questions")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        JsonNode quiz = objectMapper.readTree(questionsResponse.getResponseBody());
        String quizId = quiz.get("quizId").asText();
        JsonNode questions = quiz.get("questions");

        String q1Id = questions.get(0).get("id").asText();
        String q2Id = questions.get(1).get("id").asText();

        String q1CorrectOptionId = findOptionIdByText(questions.get(0), "Paris");
        String q2CorrectOptionId = findOptionIdByText(questions.get(1), "4");

        String payload = objectMapper.writeValueAsString(Map.of(
                "quizId", quizId,
                "answers", List.of(
                        Map.of("questionId", q1Id, "optionId", q1CorrectOptionId),
                        Map.of("questionId", q2Id, "optionId", q2CorrectOptionId)
                )
        ));

        EntityExchangeResult<String> checkResponse = authenticatedClient.post()
                .uri("/api/checkanswers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        JsonNode result = objectMapper.readTree(checkResponse.getResponseBody());
        assertThat(result.get("score").asInt()).isEqualTo(2);
        assertThat(result.get("total").asInt()).isEqualTo(2);
        assertThat(result.toString()).doesNotContain("correctOptionId");

        authenticatedClient.post()
                .uri("/api/checkanswers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Protected endpoints should return 401 without authenticated session")
    void protectedEndpointsRequireAuthentication() {
        client.get()
                .uri("/api/questions")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String findOptionIdByText(JsonNode question, String optionText) {
        for (JsonNode option : question.get("options")) {
            if (optionText.equals(option.get("text").asText())) {
                return option.get("id").asText();
            }
        }
        throw new IllegalStateException("Option text not found: " + optionText);
    }
}
