package com.clarityunited.quadtrivia.dto.quiz;

import com.clarityunited.quadtrivia.model.TriviaQuestion;
import java.util.List;

public record QuizResponse(
        String quizId,
        List<TriviaQuestion> questions
) {
}
