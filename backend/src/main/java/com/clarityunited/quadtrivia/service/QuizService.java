package com.clarityunited.quadtrivia.service;

import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaClient;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaQuestion;
import com.clarityunited.quadtrivia.client.opentrivia.OpenTriviaResponse;
import com.clarityunited.quadtrivia.dto.assesment.AnswerAssessmentResultDto;
import com.clarityunited.quadtrivia.dto.assesment.CheckAnswersResponse;
import com.clarityunited.quadtrivia.dto.quiz.QuizResponse;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedQuizAnswerRequest;
import com.clarityunited.quadtrivia.exception.QuizSessionNotFoundException;
import com.clarityunited.quadtrivia.exception.QuizSessionOwnershipException;
import com.clarityunited.quadtrivia.model.QuizSession;
import com.clarityunited.quadtrivia.model.TriviaOption;
import com.clarityunited.quadtrivia.model.TriviaQuestion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {

    private final OpenTriviaClient openTriviaClient;
    private final Duration sessionTtl;
    private final Map<String, QuizSession> sessions = new ConcurrentHashMap<>();

    public QuizService(OpenTriviaClient openTriviaClient,
                       @Value("${app.quiz.session-ttl:15m}") Duration sessionTtl) {
        this.openTriviaClient = openTriviaClient;
        this.sessionTtl = sessionTtl;
    }

    public Mono<QuizResponse> createQuizForUser(String username) {
        cleanupExpiredSessions();
        return openTriviaClient.fetchQuestions(5)
                .map(response -> buildQuizResponse(username, response));
    }

    public CheckAnswersResponse checkAnswers(String username, SubmittedQuizAnswerRequest request) {
        cleanupExpiredSessions();
        QuizSession session = sessions.get(request.quizId());

        if (session == null) {
            throw new QuizSessionNotFoundException("Quiz session not found");
        }

        if (!session.username().equals(username)) {
            throw new QuizSessionOwnershipException("Quiz session does not belong to current user");
        }

        Map<String, String> submittedByQuestion = request.answers().stream()
                .collect(HashMap::new, (map, answer) -> map.put(answer.questionId(), answer.optionId()), HashMap::putAll);

        List<AnswerAssessmentResultDto> results = new ArrayList<>();
        int score = 0;

        for (Map.Entry<String, String> entry : session.correctAnswersByQuestionId().entrySet()) {
            String questionId = entry.getKey();
            String correctOptionId = entry.getValue();
            String submittedOptionId = submittedByQuestion.get(questionId);

            boolean correct = Objects.equals(correctOptionId, submittedOptionId);
            if (correct) {
                score++;
            }

            results.add(new AnswerAssessmentResultDto(questionId, correct));
        }

        sessions.remove(request.quizId());
        return new CheckAnswersResponse(score, session.correctAnswersByQuestionId().size(), results);
    }

    private QuizResponse buildQuizResponse(String username, OpenTriviaResponse response) {
        List<OpenTriviaQuestion> sourceQuestions = response.results();
        if (sourceQuestions == null || sourceQuestions.isEmpty()) {
            throw new IllegalStateException("No trivia questions returned");
        }

        String quizId = UUID.randomUUID().toString();
        List<TriviaQuestion> questions = new ArrayList<>(sourceQuestions.size());
        Map<String, String> correctAnswersByQuestionId = new HashMap<>();

        for (OpenTriviaQuestion source : sourceQuestions) {
            String questionId = UUID.randomUUID().toString();
            String correctOptionId = UUID.randomUUID().toString();
            List<TriviaOption> options = buildOptions(source, correctOptionId);

            questions.add(new TriviaQuestion(questionId, source.question(), options));
            correctAnswersByQuestionId.put(questionId, correctOptionId);
        }

        sessions.put(quizId, new QuizSession(
                quizId,
                username,
                Instant.now(),
                correctAnswersByQuestionId
        ));

        return new QuizResponse(quizId, questions);
    }

    private List<TriviaOption> buildOptions(OpenTriviaQuestion source, String correctOptionId) {
        List<TriviaOption> options = new ArrayList<>();
        options.add(new TriviaOption(correctOptionId, source.correctAnswer()));

        for (String incorrect : source.incorrectAnswers()) {
            options.add(new TriviaOption(UUID.randomUUID().toString(), incorrect));
        }

        Collections.shuffle(options);
        return options;
    }

    public void clearSessionsForUser(String username) {
        sessions.entrySet().removeIf(entry -> entry.getValue().username().equals(username));
    }

    private void cleanupExpiredSessions() {
        Instant cutoff = Instant.now().minus(sessionTtl);
        sessions.entrySet().removeIf(entry -> entry.getValue().createdAt().isBefore(cutoff));
    }
}
