package com.clarityunited.quadtrivia.controller;

import com.clarityunited.quadtrivia.dto.assesment.AnswerAssessmentResultDto;
import com.clarityunited.quadtrivia.dto.assesment.CheckAnswersResponse;
import com.clarityunited.quadtrivia.dto.quiz.QuizResponse;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedAnswerDto;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedQuizAnswerRequest;
import com.clarityunited.quadtrivia.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizControllerTest {

    private QuizController quizController;
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = mock(QuizService.class);
        quizController = new QuizController(quizService);
    }

    @Test
    @DisplayName("GET /api/questions should return quiz response")
    void getQuestionsShouldReturnQuizResponse() {
        QuizResponse quizResponse = new QuizResponse("quiz-123", Collections.emptyList());
        when(quizService.createQuizForUser("testuser")).thenReturn(Mono.just(quizResponse));

        QuizResponse response = quizController.getQuestions(mockAuthentication("testuser")).block();
        assertEquals("quiz-123", response.quizId());
        assertEquals(0, response.questions().size());
    }

    @Test
    @DisplayName("POST /api/checkanswers should return assessment results")
    void checkAnswersShouldReturnAssessmentResults() {
        SubmittedAnswerDto answer = new SubmittedAnswerDto("q1", "o1");
        SubmittedQuizAnswerRequest request = new SubmittedQuizAnswerRequest("quiz-123", List.of(answer));
        
        AnswerAssessmentResultDto result = new AnswerAssessmentResultDto("q1", true);
        CheckAnswersResponse response = new CheckAnswersResponse(1, 1, List.of(result));
        
        when(quizService.checkAnswers(eq("testuser"), any(SubmittedQuizAnswerRequest.class)))
                .thenReturn(response);

        CheckAnswersResponse actual = quizController.checkAnswers(request, mockAuthentication("testuser"));
        assertEquals(1, actual.score());
        assertEquals(1, actual.total());
        assertEquals("q1", actual.results().getFirst().questionId());
        assertEquals(true, actual.results().getFirst().correct());
    }

    private Authentication mockAuthentication(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }
}
