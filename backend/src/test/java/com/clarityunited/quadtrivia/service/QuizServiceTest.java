package com.clarityunited.quadtrivia.service;

import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaClient;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaQuestion;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaResponse;
import com.clarityunited.quadtrivia.dto.assesment.CheckAnswersResponse;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedAnswerDto;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedQuizAnswerRequest;
import com.clarityunited.quadtrivia.exception.QuizSessionNotFoundException;
import com.clarityunited.quadtrivia.exception.QuizSessionOwnershipException;
import com.clarityunited.quadtrivia.model.TriviaOption;
import com.clarityunited.quadtrivia.model.TriviaQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("QuizService Tests")
class QuizServiceTest {

    private static final String ALICE = "alice";
    private static final String BOB = "bob";
    private static final String QUESTION_1_TEXT = "Capital of France?";
    private static final String QUESTION_2_TEXT = "2 + 2 = ?";
    private static final String QUESTION_1_CORRECT = "Paris";
    private static final String QUESTION_2_CORRECT = "4";
    private static final Map<String, String> CORRECT_BY_QUESTION_TEXT = Map.of(
            QUESTION_1_TEXT, QUESTION_1_CORRECT,
            QUESTION_2_TEXT, QUESTION_2_CORRECT
    );

    private final OpenTriviaClient openTriviaClient = mock(OpenTriviaClient.class);
    private final QuizService quizService = new QuizService(openTriviaClient, Duration.ofMinutes(15));

    @Nested
    @DisplayName("createQuizForUser")
    class CreateQuizForUser {

        @Test
        @DisplayName("Should create a quiz reactively and support full-score assessment")
        void shouldCreateQuizReactivelyAndSupportFullScoreAssessment() {
            // Arrange
            when(openTriviaClient.fetchQuestions(5)).thenReturn(Mono.just(twoQuestionResponse()));

            // Act & Assert
            StepVerifier.create(quizService.createQuizForUser(ALICE))
                    .assertNext(quizResponse -> {
                        assertThat(quizResponse.quizId()).isNotBlank();
                        assertThat(quizResponse.questions()).hasSize(2);
                        assertThat(quizResponse.questions()).allSatisfy(question ->
                                assertThat(question.options()).hasSize(4));

                        SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(
                                quizResponse.quizId(),
                                buildCorrectSubmissions(quizResponse.questions())
                        );

                        CheckAnswersResponse assessment = quizService.checkAnswers(ALICE, request);
                        assertThat(assessment.score()).isEqualTo(2);
                        assertThat(assessment.total()).isEqualTo(2);
                        assertThat(assessment.results()).allSatisfy(result -> {
                            assertThat(result.correct()).isTrue();
                            assertThat(result.questionId()).isNotBlank();
                        });
                    })
                    .verifyComplete();

            verify(openTriviaClient).fetchQuestions(5);
        }

        @Test
        @DisplayName("Should fail when trivia response has no questions")
        void shouldFailWhenTriviaResponseHasNoQuestions() {
            // Arrange
            when(openTriviaClient.fetchQuestions(5)).thenReturn(Mono.just(new OpenTriviaResponse(0, List.of())));

            // Act & Assert
            StepVerifier.create(quizService.createQuizForUser(ALICE))
                    .expectErrorMatches(error -> error instanceof IllegalStateException
                            && error.getMessage().contains("No trivia questions returned"))
                    .verify();
        }
    }

    @Nested
    @DisplayName("checkAnswers")
    class CheckAnswers {

        @Test
        @DisplayName("Should calculate partial score for mixed correct and incorrect answers")
        void shouldCalculatePartialScoreForMixedAnswers() {
            // Arrange
            when(openTriviaClient.fetchQuestions(5)).thenReturn(Mono.just(twoQuestionResponse()));

            // Act & Assert
            StepVerifier.create(quizService.createQuizForUser(ALICE))
                    .assertNext(quizResponse -> {
                        List<SubmittedAnswerDto> answers = buildMixedSubmissions(quizResponse.questions());
                        SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(quizResponse.quizId(), answers);

                        CheckAnswersResponse assessment = quizService.checkAnswers(ALICE, request);
                        assertThat(assessment.total()).isEqualTo(2);
                        assertThat(assessment.score()).isEqualTo(1);
                        assertThat(assessment.results()).hasSize(2);
                        assertThat(assessment.results().stream().filter(result -> result.correct()).count()).isEqualTo(1);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should reject unknown quiz session")
        void shouldRejectUnknownQuizSession() {
            // Arrange
            SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(
                    "unknown-quiz-id",
                    List.of(new SubmittedAnswerDto("q1", "o1"))
            );

            // Act & Assert
            assertThatThrownBy(() -> quizService.checkAnswers(ALICE, request))
                    .isInstanceOf(QuizSessionNotFoundException.class)
                    .hasMessage("Quiz session not found");
        }

        @Test
        @DisplayName("Should reject answers from a different user")
        void shouldRejectAnswersFromDifferentUser() {
            // Arrange
            when(openTriviaClient.fetchQuestions(5)).thenReturn(Mono.just(twoQuestionResponse()));

            // Act & Assert
            StepVerifier.create(quizService.createQuizForUser(ALICE))
                    .assertNext(quizResponse -> {
                        SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest(
                                quizResponse.quizId(),
                                buildCorrectSubmissions(quizResponse.questions())
                        );

                        assertThatThrownBy(() -> quizService.checkAnswers(BOB, request))
                                .isInstanceOf(QuizSessionOwnershipException.class)
                                .hasMessage("Quiz session does not belong to current user");
                    })
                    .verifyComplete();
        }
    }

    private OpenTriviaResponse twoQuestionResponse() {
        return new OpenTriviaResponse(0, List.of(
                new OpenTriviaQuestion(
                        "General Knowledge",
                        "multiple",
                        "easy",
                        QUESTION_1_TEXT,
                        QUESTION_1_CORRECT,
                        List.of("London", "Berlin", "Madrid")
                ),
                new OpenTriviaQuestion(
                        "Math",
                        "multiple",
                        "easy",
                        QUESTION_2_TEXT,
                        QUESTION_2_CORRECT,
                        List.of("3", "5", "6")
                )
        ));
    }

    private List<SubmittedAnswerDto> buildCorrectSubmissions(List<TriviaQuestion> questions) {
        return questions.stream()
                .map(question -> new SubmittedAnswerDto(
                        question.id(),
                        optionIdForText(question, CORRECT_BY_QUESTION_TEXT.get(question.text()))
                ))
                .toList();
    }

    private List<SubmittedAnswerDto> buildMixedSubmissions(List<TriviaQuestion> questions) {
        Map<String, TriviaQuestion> byQuestionText = questions.stream()
                .collect(Collectors.toMap(TriviaQuestion::text, Function.identity()));

        TriviaQuestion firstQuestion = byQuestionText.get(QUESTION_1_TEXT);
        TriviaQuestion secondQuestion = byQuestionText.get(QUESTION_2_TEXT);

        return List.of(
                new SubmittedAnswerDto(firstQuestion.id(), optionIdForText(firstQuestion, QUESTION_1_CORRECT)),
                new SubmittedAnswerDto(secondQuestion.id(), incorrectOptionId(secondQuestion, QUESTION_2_CORRECT))
        );
    }

    private String optionIdForText(TriviaQuestion question, String text) {
        return question.options().stream()
                .filter(option -> option.text().equals(text))
                .findFirst()
                .map(TriviaOption::id)
                .orElseThrow(() -> new AssertionError("Expected option text not found: " + text));
    }

    private String incorrectOptionId(TriviaQuestion question, String correctText) {
        return question.options().stream()
                .filter(option -> !option.text().equals(correctText))
                .findFirst()
                .map(TriviaOption::id)
                .orElseThrow(() -> new AssertionError("Expected at least one incorrect option"));
    }
}
