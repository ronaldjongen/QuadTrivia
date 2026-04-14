package com.clarityunited.quadtrivia.model;

import java.time.Instant;
import java.util.Map;

public record QuizSession(
        String quizId,
        String username,
        Instant createdAt,
        Map<String, String> correctAnswersByQuestionId
) {
}
